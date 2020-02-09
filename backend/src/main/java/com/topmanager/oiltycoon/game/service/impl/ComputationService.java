package com.topmanager.oiltycoon.game.service.impl;

import com.topmanager.oiltycoon.game.model.Player;
import com.topmanager.oiltycoon.game.model.Room;
import com.topmanager.oiltycoon.game.model.game.GamePeriodData;
import com.topmanager.oiltycoon.game.model.game.company.*;
import com.topmanager.oiltycoon.game.model.game.scenario.Scenario;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComputationService {

    public CompanyPeriodData calculateCompany(CompanyPeriodData companyOld, CompanyPeriodData companyNew, Room roomData) {
        CompanyStore storeOld = companyOld.getStore();
        CompanyStore storeNew = companyNew.getStore();
        CompanySolutions solutions = companyNew.getSolutions();
        Scenario scenario = roomData.getScenario();

        storeNew.setStorageCost(storeOld.getStorage()); // стоимость хранения = склад
        companyNew.setFullPower(companyOld.getFuturePower());

        // если спрос > предложения
        if (solutions.getProduction() + storeOld.getStorage() < storeNew.getReceivedOrders()) {
            storeNew.setSales(solutions.getProduction() + storeOld.getStorage());
            storeNew.setStorage(0);
            storeNew.setBacklogSales(storeNew.getReceivedOrders() - storeOld.getSales());
        } else {
            storeNew.setSales(storeNew.getReceivedOrders());
            storeNew.setStorage(storeOld.getStorage() - storeNew.getReceivedOrders() + solutions.getProduction());
            storeNew.setBacklogSales(0);
        }

        // Выручка
        companyNew.setRevenue(solutions.getPrice() * storeNew.getSales());

        // амортизация
        if (solutions.getInvestments() < storeOld.getMachineTools() * 2) {
            int machines = storeOld.getMachineTools();
            storeNew.setMachineTools(machines - (machines * 2 - solutions.getInvestments()) / 40);
        }
        companyNew.setAmortization(storeNew.getMachineTools() * 2);
        companyNew.setAdditionalInvestments(
                Math.max(0, solutions.getInvestments() - companyNew.getAmortization())
        );

        // мощность след.периода
        companyNew.setFuturePower(
                (companyNew.getAdditionalInvestments() + storeNew.getMachineTools() * 40) / 40
        );
        storeNew.setMachineTools(companyNew.getFuturePower());

        // используемая мощность в %
        companyNew.setUsingPower((double) solutions.getProduction() / companyNew.getFullPower());
        companyNew.setSPPT((int) (storeNew.getSales() * companyOld.getProductionCost()));

        double usingPower = companyNew.getUsingPower();
        double coefficient = 5d * Math.pow(usingPower, 2d) - 8d * usingPower + 4.2d;
        if (usingPower == 0d) {
            coefficient = 1d;
        }
        companyNew.setProductionCost(
                (((4200d / roomData.getMaxPlayers()) / companyNew.getFuturePower()) * 15d + 3) * coefficient
        );
        companyNew.setUsingPower(companyNew.getUsingPower() * 100);

        int expenses = (int) (solutions.getInvestments() + solutions.getNir() + solutions.getMarketing()
                + companyNew.getProductionCost() * solutions.getProduction());

        // займы
        companyNew.setLoan(companyOld.getLoan());
        companyNew.setBank(companyOld.getBank());
        if (companyNew.getBank() - expenses < 0) {
            companyNew.setLoan(companyNew.getLoan() + companyNew.getBank() - expenses);
        }
        if (companyNew.getLoan() < scenario.getLoanLimit()) {
            companyNew.setBankInterest(
                    (int) Math.round(companyNew.getLoan() * scenario.getBankRate() / 4d)
            );
        } else {
            companyNew.setBankInterest(
                    (int) Math.round(scenario.getLoanLimit() * scenario.getBankRate() / 4d
                            + (companyNew.getLoan() - scenario.getLoanLimit()) * scenario.getExtraBankRate() / 4d)
            );
        }

        // тайные отнимания
        companyNew.setProductionCostAll(
                (int) (companyNew.getProductionCost() * solutions.getProduction())
        );
        companyNew.setGrossIncome(companyNew.getRevenue() - companyNew.getSPPT());
        companyNew.setProfitTax(companyNew.getRevenue() -
                (storeNew.getStorageCost()
                        + companyNew.getBankInterest()
                        + companyNew.getSPPT()
                        + companyNew.getAmortization()
                        + solutions.getMarketing()
                        + solutions.getNir())
        );

        if (companyNew.getBank() < companyNew.getAdditionalInvestments()) {
            companyNew.setLoan(companyNew.getLoan() + companyNew.getAdditionalInvestments() - companyNew.getBank());
            companyNew.setBank(0);
        } else {
            companyNew.setBank(companyNew.getBank() - companyNew.getAdditionalInvestments());
        }

        // Прибыль до налога...
        companyNew.setTax(Math.max((int) Math.round(companyNew.getProfitTax() * 0.25d), 0));
        companyNew.setNetProfit(companyNew.getProfitTax() - companyNew.getTax());

        if (companyNew.getNetProfit() > 0) {
            int loanIncome = (int) (0.75d * companyNew.getNetProfit());
            companyNew.setLoan(companyNew.getLoan() - loanIncome);
            companyNew.setBank(companyNew.getBank() + companyNew.getNetProfit() - loanIncome);
        } else {
            companyNew.setBank(companyNew.getBank() + companyNew.getNetProfit());
            if (companyNew.getBank() < 0) {
                companyNew.setLoan(companyNew.getLoan() - companyNew.getBank());
                companyNew.setBank(0);
            }
        }

        companyNew.setAccumulatedProfit(companyOld.getAccumulatedProfit() + companyNew.getNetProfit());
        companyNew.setActiveStorage((int) (storeNew.getStorage() * companyNew.getProductionCost()));
        companyNew.setKapInvests(companyNew.getFullPower() * 40);
        companyNew.setSumActives(companyOld.getKapInvests() + companyNew.getActiveStorage() + companyNew.getBank());

        companyNew.setSumMarketing(companyOld.getSumMarketing() + solutions.getMarketing());
        companyNew.setSumNir(companyOld.getSumNir() + solutions.getNir());
        companyNew.setSumProduction(companyOld.getSumProduction() + solutions.getProduction());

        companyNew.setInitialAccumulatedProfit(companyOld.getInitialAccumulatedProfit());
        return companyNew;
    }

    public GamePeriodData calculatePeriod(Room room, int currentPeriod) {
        GamePeriodData period = new GamePeriodData();
        GamePeriodData previousPeriod = room.getPeriodDataByPeriod(currentPeriod - 1);
        List<Company> companies = room.getPlayers().values()
                .stream()
                .filter((player) -> !player.isBankrupt())
                .map(Player::getCompany)
                .collect(Collectors.toList());

        period.setSummaryMarketing(previousPeriod.getSummaryMarketing());
        period.setSummaryNir(previousPeriod.getSummaryNir());
        period.setSummaryProduction(previousPeriod.getSummaryProduction());

        companies.forEach((company) -> {
            CompanyPeriodData companyData = company.getDataByPeriod(currentPeriod);
            CompanySolutions solutions = companyData.getSolutions();
            period.addSummaryPeriodMarketing(solutions.getMarketing());
            period.addAveragePeriodPrice(solutions.getPrice());
            period.addSummaryNir(solutions.getNir());
            period.addSummaryMarketing(solutions.getMarketing());
            period.addSummaryProduction(solutions.getProduction());

            period.addTotalMarketing(Math.pow((double) solutions.getMarketing() / solutions.getPrice(), 1.5d));
            period.addTotalPrice(Math.pow(1d / solutions.getPrice(), 3d));

            companyData.setMaxPredictedSales(
                    company.getDataByPeriod(currentPeriod - 1).getStore().getStorage() + solutions.getProduction()
            );
            companyData.setMaxPredictedRevenue(solutions.getPrice() * companyData.getMaxPredictedSales());
        });

        double sumMkCompressed = Math.min(
                0.25d * (period.getSummaryPeriodMarketing() - 2100d * 2d * companies.size()) + 2100d * 2d * companies.size(),
                period.getSummaryMarketing()
        );
        period.setAveragePeriodPrice(period.getAveragePeriodPrice() / companies.size());

        double averagePriceGiven = period.getAveragePeriodPrice();
        double averagePricePlanned;
        int sumMaxPredictedSales = companies.stream()
                .map(company -> company.getDataByPeriod(currentPeriod).getMaxPredictedSales())
                .reduce(0, Integer::sum);

        if (sumMaxPredictedSales == 0) {
            averagePricePlanned = averagePriceGiven;
        } else {
            int sumMaxPredictedRevenue = companies.stream()
                    .map(company -> company.getDataByPeriod(currentPeriod).getMaxPredictedRevenue())
                    .reduce(0, Integer::sum);
            averagePricePlanned = (double) sumMaxPredictedRevenue / sumMaxPredictedSales;
        }

        double demandEffectMk = 5.3d
                * Math.sqrt(sumMkCompressed / 8400d)
                / (averagePricePlanned / 30d);
        double demandEffectRd = period.getSummaryNir() / (currentPeriod + 1d) / 3360d;
        double ordersDemand = 62.5d * 10d * (demandEffectRd + demandEffectMk);

        companies.forEach((company) -> {
            CompanyPeriodData companyData = company.getDataByPeriod(currentPeriod);
            CompanySolutions solutions = companyData.getSolutions();
            companyData.setShareEffectPr(Math.pow(averagePricePlanned / solutions.getPrice(), 3d));
            companyData.setShareEffectMk(Math.pow((double) solutions.getMarketing() / solutions.getPrice(), 1.5d));
            companyData.setShareEffectRd(companyData.getSumNir());
        });

        double sumShareEffectPr = 0d;
        double sumShareEffectMk = 0d;
        double sumShareEffectRd = 0d;

        for (Company company : companies) {
            CompanyPeriodData periodData = company.getDataByPeriod(currentPeriod);
            sumShareEffectPr += periodData.getShareEffectPr();
            sumShareEffectMk += periodData.getShareEffectMk();
            sumShareEffectRd += periodData.getShareEffectRd();
        }

        for (Company company : companies) {
            CompanyPeriodData periodData = company.getDataByPeriod(currentPeriod);
            CompanySolutions solutions = periodData.getSolutions();
            double ordersShare = 0.7d * periodData.getShareEffectPr() / sumShareEffectPr
                    + 0.15d * periodData.getShareEffectMk() / sumShareEffectMk
                    + 0.15d * periodData.getShareEffectRd() / sumShareEffectRd;
            double ordersShareCompressed = Math.min(ordersShare * 40d / solutions.getPrice(), ordersShare);
            int companyOrders = (int) (ordersDemand * ordersShareCompressed);
            period.addTotalBuyers(companyOrders);
        }

        int sumMarketing = period.getSummaryMarketing();
        period.setSummaryPeriodMarketing(
                sumMarketing > 16800 ? Math.sqrt(sumMarketing / 4d + 12600) : Math.sqrt(sumMarketing)
        );

        // calculate total buyers amount
        period.setTotalBuyers((int) ((
                period.getSummaryPeriodMarketing() / period.getAveragePeriodPrice()) * 936.2d
                + period.getSummaryNir() / (6.72d * (room.getCurrentPeriod() + 2))
        ));
        if (period.getAveragePeriodPrice() > 40d) {
            period.setTotalBuyers((int) (period.getTotalBuyers() * 40d / period.getAveragePeriodPrice()));
        }

        // calculate buyers for each company
        int buyersForRichPrice = 0;
        for (Company company : companies) {
            CompanyPeriodData periodData = company.getDataByPeriod(currentPeriod);
            CompanySolutions solutions = periodData.getSolutions();
            int companyBuyers = getBuyersForCompany(periodData, period, period.getTotalBuyers());
            periodData.getStore().setReceivedOrders((int) (companyBuyers * 40d / solutions.getPrice()));
            buyersForRichPrice += (companyBuyers - periodData.getStore().getReceivedOrders());
        }

        if (buyersForRichPrice > 0) {
            for (Company company : companies) {
                CompanyPeriodData periodData = company.getDataByPeriod(currentPeriod);
                int additionalCompanyBuyers = getBuyersForCompany(periodData, period, buyersForRichPrice);
                periodData.getStore().setReceivedOrders(periodData.getStore().getReceivedOrders() + additionalCompanyBuyers);
            }
        }

        // calculate players
        for (Company company : companies) {
            CompanyPeriodData currentPeriodCompany = calculateCompany(
                    company.getDataByPeriod(currentPeriod - 1), company.getDataByPeriod(currentPeriod), room
            );

            period.addTotalSales(currentPeriodCompany.getStore().getSales());
            period.addSummaryPeriodPower(currentPeriodCompany.getFullPower());
            period.addSummaryPeriodProduction(currentPeriodCompany.getSolutions().getProduction());
            period.addSummaryPeriodStorage(currentPeriodCompany.getStore().getStorage());
            period.addSummaryPeriodRevenue(currentPeriodCompany.getRevenue());
            period.addAveragePeriodProductionCost(currentPeriodCompany.getProductionCost());
            period.addAveragePeriodUsingPower(currentPeriodCompany.getUsingPower());
            period.addSummaryPeriodKapInvests(currentPeriodCompany.getKapInvests());
        }
        period.setAveragePeriodProductionCost(period.getAveragePeriodProductionCost() / companies.size());
        period.setAveragePeriodUsingPower(period.getAveragePeriodUsingPower() / companies.size());

        for (Company company : companies) {
            CompanyPeriodData companyData = company.getDataByPeriod(currentPeriod);
            if (period.getTotalBuyers() > 0) {
                companyData.setMarketingPart(
                        (double) companyData.getStore().getReceivedOrders() / period.getTotalBuyers() * 100d
                );
            }
            int playersAmount = companies.size();
            double ratingByAccumulatedProfit =
                    (50 + Math.pow(companyData.getAccumulatedProfit() - companyData.getInitialAccumulatedProfit(), 3d) / 54d);
            double ratingByDemand = ((double) (companyData.getSumMarketing() + companyData.getSumNir())
                    / (period.getSummaryMarketing() + period.getSummaryNir())
                    * playersAmount * 10d
            );
            double ratingBySupply = (
                    ((double) companyData.getSumProduction() / period.getSummaryProduction()) * playersAmount * 10d
            );
            double ratingByEfficiency = ((1d - Math.abs(companyData.getUsingPower() - 80d) / 100d) * 10d);
            double ratingByMarketingPart = (Math.min(
                    ((double) companyData.getStore().getReceivedOrders() / period.getTotalBuyers() * playersAmount * 10d),
                    20d
            ));
            int sales = companyData.getStore().getSales();
            double ratingByGrow = (Math.min(
                    sales == 0 ? 0d : 10d * sales / companyData.getStore().getSalesOld()
                            / period.getTotalSales() * previousPeriod.getTotalSales(),
                    10d
            ));
            companyData.setRating((int) (ratingByAccumulatedProfit
                    + ratingByDemand
                    + ratingBySupply
                    + ratingByEfficiency
                    + ratingByMarketingPart
                    + ratingByGrow
            ));

            if (sales > 0) {
                companyData.getStore().setSalesOld(sales);
            }
        }
        return period;
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
