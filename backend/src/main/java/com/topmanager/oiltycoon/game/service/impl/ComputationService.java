package com.topmanager.oiltycoon.game.service.impl;

import com.topmanager.oiltycoon.game.model.Player;
import com.topmanager.oiltycoon.game.model.Room;
import com.topmanager.oiltycoon.game.model.game.GamePeriodData;
import com.topmanager.oiltycoon.game.model.game.company.*;
import com.topmanager.oiltycoon.game.model.game.scenario.Scenario;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComputationService {

    public void calculateCompany(CompanyPeriodData company, Room roomData) {
        CompanyStore store = company.getStore();
        CompanySolutions solutions = company.getSolutions();
        Scenario scenario = roomData.getScenario();

        store.setStorageCost(store.getStorage()); // стоимость хранения = склад
        company.setFullPower(company.getFuturePower());

        // если спрос > предложения
        if (solutions.getProduction() + store.getStorage() < store.getReceivedOrders()) {
            store.setSales(solutions.getProduction() + store.getStorage());
            store.setStorage(0);
            store.setBacklogSales(store.getReceivedOrders() - store.getSales());
        } else {
            store.setSales(store.getReceivedOrders());
            store.setStorage(store.getStorage() - store.getReceivedOrders() + solutions.getProduction());
            store.setBacklogSales(0);
        }

        // Выручка
        company.setRevenue(solutions.getPrice() * store.getSales());

        // амортизация
        if (solutions.getInvestments() < store.getMachineTools() * 2) {
            int machines = store.getMachineTools();
            store.setMachineTools(machines - (machines * 2 - solutions.getInvestments()) / 40);
        }
        company.setAmortization(store.getMachineTools() * 2);
        company.setAdditionalInvestments(
                Math.max(0, solutions.getInvestments() - company.getAmortization())
        );

        // мощность след.периода
        company.setFuturePower(
                (company.getAdditionalInvestments() + store.getMachineTools() * 40) / 40
        );
        store.setMachineTools(company.getFuturePower());

        // используемая мощность в %
        company.setUsingPower((double) solutions.getProduction() / company.getFullPower());
        company.setSPPT((int) (store.getSales() * company.getProductionCost()));

        double usingPower = company.getUsingPower();
        double coefficient = 5d * Math.pow(usingPower, 2d) - 8d * usingPower + 4.2d;
        if (usingPower == 0d) {
            coefficient = 1d;
        }
        company.setProductionCost(
                (((4200d / roomData.getMaxPlayers()) / company.getFuturePower()) * 15d + 3) * coefficient
        );
        company.setUsingPower(company.getUsingPower() * 100);

        int expenses = (int) (solutions.getInvestments() + solutions.getNir() + solutions.getMarketing()
                + company.getProductionCost() * solutions.getProduction());

        // займы
        if (company.getBank() - expenses < 0) {
            company.setLoan(company.getLoan() + company.getBank() - expenses);
        }
        if (company.getLoan() < scenario.getLoanLimit()) {
            company.setBankInterest(
                    (int) Math.round(company.getLoan() * scenario.getBankRate() / 4d)
            );
        } else {
            company.setBankInterest(
                    (int) Math.round(scenario.getLoanLimit() * scenario.getBankRate() / 4d
                            + (company.getLoan() - scenario.getLoanLimit()) * scenario.getExtraBankRate() / 4d)
            );
        }

        // тайные отнимания
        company.setProductionCostAll(
                (int) (company.getProductionCost() * solutions.getProduction())
        );
        company.setGrossIncome(company.getRevenue() - company.getSPPT());
        company.setProfitTax(company.getRevenue() -
                (store.getStorageCost()
                        + company.getBankInterest()
                        + company.getSPPT()
                        + company.getAmortization()
                        + solutions.getMarketing()
                        + solutions.getNir())
        );

        if (company.getBank() < company.getAdditionalInvestments()) {
            company.setLoan(company.getLoan() + company.getAdditionalInvestments() - company.getBank());
            company.setBank(0);
        } else {
            company.setBank(company.getBank() - company.getAdditionalInvestments());
        }

        // Прибыль до налога...
        company.setTax(Math.max((int) Math.round(company.getProfitTax() * 0.25d), 0));
        company.setNetProfit(company.getProfitTax() - company.getTax());

        if (company.getNetProfit() > 0) {
            int loanIncome = (int) (0.75d * company.getNetProfit());
            company.setLoan(company.getLoan() - loanIncome);
            company.setBank(company.getBank() + company.getNetProfit() - loanIncome);
        } else {
            company.setBank(company.getBank() + company.getNetProfit());
            if (company.getBank() < 0) {
                company.setLoan(company.getLoan() - company.getBank());
                company.setBank(0);
            }
        }

        company.setAccumulatedProfit(company.getAccumulatedProfit() + company.getNetProfit());
        company.setActiveStorage((int) (store.getStorage() * company.getProductionCost()));
        company.setKapInvests(company.getFullPower() * 40);
        company.setSumActives(company.getKapInvests() + company.getActiveStorage() + company.getBank());

        company.setSumMarketing(company.getSumMarketing() + solutions.getMarketing());
        company.setSumNir(company.getSumNir() + solutions.getNir());
        company.setSumProduction(company.getSumProduction() + solutions.getProduction());
    }

    public void calculatePeriod(Room room, int currentPeriod) {
        GamePeriodData period = room.getPeriodDataByPeriod(currentPeriod);
        GamePeriodData previousPeriod = room.getPeriodDataByPeriod(currentPeriod - 1);
        List<CompanyPeriodData> players = room.getPlayers().values()
                .stream()
                .filter((player) -> !player.isBankrupt())
                .map((player) -> player.getCompany().getDataByPeriod(currentPeriod))
                .collect(Collectors.toList());

        period.setSummaryMarketing(previousPeriod.getSummaryMarketing());
        period.setSummaryNir(previousPeriod.getSummaryNir());
        period.setSummaryProduction(previousPeriod.getSummaryProduction());

        players.forEach((company) -> {
            CompanySolutions solutions = company.getSolutions();
            period.addSummaryPeriodMarketing(solutions.getMarketing());
            period.addAveragePeriodPrice(solutions.getPrice());
            period.addSummaryNir(solutions.getNir());
            period.addSummaryMarketing(solutions.getMarketing());
            period.addSummaryProduction(solutions.getProduction());

            period.addTotalMarketing(Math.pow((double) solutions.getMarketing() / solutions.getPrice(), 1.5d));
            period.addTotalPrice(Math.pow(1d / solutions.getPrice(), 3d));

            company.setMaxPredictedSales(company.getStore().getStorage() + solutions.getProduction());
            company.setMaxPredictedRevenue(solutions.getPrice() * company.getMaxPredictedSales());
        });

        double sumMkCompressed = Math.min(
                0.25f * (period.getSummaryPeriodMarketing() - 2100 * 2 * players.size()) + 2100 * 2 * players.size(),
                period.getSummaryMarketing()
        );

        double averagePriceGiven = period.getAveragePeriodPrice() / players.size();
        double averagePricePlanned;
        int sumMaxPredictedSales = players.stream()
                .map(CompanyPeriodData::getMaxPredictedSales)
                .reduce(0, Integer::sum);

        if (sumMaxPredictedSales == 0) {
            averagePricePlanned = averagePriceGiven;
        } else {
            int sumMaxPredictedRevenue = players.stream()
                    .map(CompanyPeriodData::getMaxPredictedRevenue)
                    .reduce(0, Integer::sum);
            averagePricePlanned = (double) sumMaxPredictedRevenue / sumMaxPredictedSales;
        }

        double demandEffectMk = 5.3f
                * Math.sqrt(sumMkCompressed / 8400d)
                / (averagePricePlanned / 30d);
        double demandEffectRd = period.getSummaryNir() / (currentPeriod + 1d) / 3360f;
        double ordersDemand = 62.5f * 10 * (demandEffectRd + demandEffectMk);

        players.forEach((company) -> {
            CompanySolutions solutions = company.getSolutions();
            company.setShareEffectPr(Math.pow(averagePricePlanned / solutions.getPrice(), 3d));
            company.setShareEffectMk(Math.pow((double) solutions.getMarketing() / solutions.getPrice(), 1.5d));
            company.setShareEffectRd(company.getSumNir());
        });

        double sumShareEffectPr = players.stream().map(CompanyPeriodData::getShareEffectPr).reduce(0d, Double::sum);
        double sumShareEffectMk = players.stream().map(CompanyPeriodData::getShareEffectMk).reduce(0d, Double::sum);
        double sumShareEffectRd = players.stream().map(CompanyPeriodData::getShareEffectRd).reduce(0d, Double::sum);

        players.forEach((company) -> {
            CompanySolutions solutions = company.getSolutions();
            double ordersShare = 0.7d * company.getShareEffectPr() / sumShareEffectPr
                    + 0.15d * company.getShareEffectMk() / sumShareEffectMk
                    + 0.15d * company.getShareEffectRd() / sumShareEffectRd;
            double ordersShareCompressed = Math.min(ordersShare * 40d / solutions.getPrice(), ordersShare);
            int companyOrders = (int) (ordersDemand * ordersShareCompressed);
            period.addTotalBuyers(companyOrders);
        });

        period.setAveragePeriodPrice(period.getAveragePeriodPrice() / players.size());
        int sumMarketing = period.getSummaryMarketing();
        period.setSummaryPeriodMarketing(
                sumMarketing > 16800 ? Math.sqrt(sumMarketing / 4d + 12600) : Math.sqrt(sumMarketing)
        );

        // calculate total buyers amount
        period.setTotalBuyers((int) ((
                period.getSummaryPeriodMarketing() / period.getAveragePeriodPrice()) * 936.2d
                + period.getSummaryNir() / (6.72d * (room.getCurrentRound() + 2))
        ));
        if (period.getAveragePeriodPrice() > 40d) {
            period.setTotalBuyers((int) (period.getTotalBuyers() * 40d / period.getAveragePeriodPrice()));
        }

        // calculate buyers for each company
        int buyersForRichPrice = 0;
        for (CompanyPeriodData company : players) {
            CompanySolutions solutions = company.getSolutions();
            int companyBuyers = getBuyersForCompany(company, period, period.getTotalBuyers());
            company.getStore().setReceivedOrders((int) (companyBuyers * 40d / solutions.getPrice()));
            buyersForRichPrice += (companyBuyers - company.getStore().getReceivedOrders());
        }

        if (buyersForRichPrice > 0) {
            for (CompanyPeriodData company : players) {
                int additionalCompanyBuyers = getBuyersForCompany(company, period, buyersForRichPrice);
                company.getStore().setReceivedOrders(company.getStore().getReceivedOrders() + additionalCompanyBuyers);
            }
        }

        // calculate players
        for (CompanyPeriodData company : players) {
            calculateCompany(company, room);

            period.addTotalSales(company.getStore().getSales());
            period.addSummaryPeriodPower(company.getFullPower());
            period.addSummaryPeriodProduction(company.getSolutions().getProduction());
            period.addSummaryPeriodStorage(company.getStore().getStorage());
            period.addSummaryPeriodRevenue(company.getRevenue());
            period.addAveragePeriodProductionCost(company.getProductionCost());
            period.addAveragePeriodUsingPower(company.getUsingPower());
            period.addSummaryPeriodKapInvests(company.getKapInvests());
        }
        period.setAveragePeriodProductionCost(period.getAveragePeriodProductionCost() / players.size());
        period.setAveragePeriodUsingPower(period.getAveragePeriodUsingPower() / players.size());

        for (CompanyPeriodData company : players) {
            if (period.getTotalBuyers() > 0) {
                company.setMarketingPart(
                        (double) company.getStore().getReceivedOrders() / period.getTotalBuyers() * 100d
                );
            }
            int playersAmount = players.size();
            double ratingByAccumulatedProfit =
                    (50 + Math.pow(company.getAccumulatedProfit() - company.getInitialAccumulatedProfit(), 3d) / 54d);
            double ratingByDemand = ((double) (company.getSumMarketing() + company.getSumNir())
                    / (period.getSummaryMarketing() + period.getSummaryNir())
                    * playersAmount * 10d
            );
            double ratingBySupply = (
                    ((double) company.getSumProduction() / period.getSummaryProduction()) * playersAmount * 10d
            );
            double ratingByEfficiency = ((1d - Math.abs(company.getUsingPower() - 80d) / 100d) * 10d);
            double ratingByMarketingPart = (Math.min(
                    ((double) company.getStore().getReceivedOrders() / period.getTotalBuyers() * playersAmount * 10d),
                    20d
            ));
            int sales = company.getStore().getSales();
            double ratingByGrow = (Math.min(
                    sales == 0 ? 0d : 10d * sales / company.getStore().getSalesOld()
                            / period.getTotalSales() * previousPeriod.getTotalSales(),
                    10d
            ));
            company.setRating((int) (ratingByAccumulatedProfit
                    + ratingByDemand
                    + ratingBySupply
                    + ratingByEfficiency
                    + ratingByMarketingPart
                    + ratingByGrow
            ));

            if(sales > 0) {
                company.getStore().setSalesOld(sales);
            }
        }

    }

    private int getBuyersForCompany(CompanyPeriodData company, GamePeriodData period, int industryBuyersAmount) {
        CompanySolutions solutions = company.getSolutions();
        double nir = 0;
        double marketing = 0;
        double price = industryBuyersAmount * 0.7d * Math.pow(1d / solutions.getPrice(), 3d) / period.getTotalPrice();

        if (period.getSummaryNir() > 0) {
            nir = industryBuyersAmount * 0.15d * company.getSumNir() / period.getSummaryNir();
        }

        if (period.getTotalMarketing() > 0) {
            marketing = industryBuyersAmount * 0.15d *
                    Math.pow((double) solutions.getMarketing() / solutions.getPrice(), 1.5d) / period.getTotalMarketing();
        }
        return (int) (marketing + price + nir);
    }
}
