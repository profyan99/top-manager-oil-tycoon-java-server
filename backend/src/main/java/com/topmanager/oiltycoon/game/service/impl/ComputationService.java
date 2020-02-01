package com.topmanager.oiltycoon.game.service.impl;

import com.topmanager.oiltycoon.game.model.Room;
import com.topmanager.oiltycoon.game.model.game.company.CompanyPeriodData;
import com.topmanager.oiltycoon.game.model.game.company.CompanySolutions;
import com.topmanager.oiltycoon.game.model.game.company.CompanyStore;
import com.topmanager.oiltycoon.game.model.game.scenario.Scenario;
import org.springframework.stereotype.Service;

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

        if(company.getBank() < company.getAdditionalInvestments()) {
            company.setLoan(company.getLoan() + company.getAdditionalInvestments() - company.getBank());
            company.setBank(0);
        } else {
            company.setBank(company.getBank() - company.getAdditionalInvestments());
        }

        // Прибыль до налога...
        company.setTax(Math.max((int) Math.round(company.getProfitTax() * 0.25d), 0));
        company.setNetProfit(company.getProfitTax() - company.getTax());

        if(company.getNetProfit() > 0) {
            int loanIncome = (int) (0.75d * company.getNetProfit());
            company.setLoan(company.getLoan() - loanIncome);
            company.setBank(company.getBank() + company.getNetProfit() - loanIncome);
        } else {
            company.setBank(company.getBank() + company.getNetProfit());
            if(company.getBank() < 0) {
                company.setLoan(company.getLoan() - company.getBank());
                company.setBank(0);
            }
        }

        company.setAccumulatedProfit(company.getAccumulatedProfit() + company.getNetProfit());
        company.setActiveStorage((int) (store.getStorage() * company.getProductionCost()));
        company.setKapInvests(company.getFullPower() * 40);
        company.setSumActives(company.getKapInvests() + company.getActiveStorage() + company.getBank());
    }

    public void calculatePeriod() {

    }
}
