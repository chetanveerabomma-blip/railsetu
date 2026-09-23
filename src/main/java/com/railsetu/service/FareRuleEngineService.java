package com.railsetu.service;

import com.railsetu.domain.CoachClass;
import com.railsetu.domain.FareRule;
import com.railsetu.domain.SystemConfiguration;
import com.railsetu.repository.FareRuleRepository;
import com.railsetu.repository.SystemConfigurationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class FareRuleEngineService {

    private final FareRuleRepository fareRuleRepository;
    private final SystemConfigurationRepository configRepository;

    public FareRuleEngineService(FareRuleRepository fareRuleRepository, SystemConfigurationRepository configRepository) {
        this.fareRuleRepository = fareRuleRepository;
        this.configRepository = configRepository;
    }

    public static class FareCalculationResult {
        public double baseFare;
        public double classCharge;
        public double reservationCharge;
        public double serviceCharge;
        public double dynamicComponent;
        public double discount;
        public double finalFare;
        public double occupancyPercent;
        public String ruleAppliedTrace;
        public String simulationNote;
    }

    public FareCalculationResult evaluateFare(String trainType,
                                             CoachClass coachClass,
                                             int distanceKm,
                                             LocalDate journeyDate,
                                             double currentOccupancyPercent,
                                             int passengerCount) {

        FareCalculationResult result = new FareCalculationResult();
        result.occupancyPercent = currentOccupancyPercent;

        // Retrieve active rules sorted by priority
        List<FareRule> rules = fareRuleRepository.findByActiveTrueOrderByPriorityAsc();

        double baseRateKm = 0.50; // default base rate
        double reservation = 40.0;
        double service = 25.0;
        double dynamicMult = 1.0;
        double concession = 0.0;
        StringBuilder trace = new StringBuilder();

        for (FareRule rule : rules) {
            boolean matchesTrain = "ALL".equalsIgnoreCase(rule.getAppliesToTrainType()) ||
                    rule.getAppliesToTrainType().equalsIgnoreCase(trainType);
            boolean matchesClass = "ALL".equalsIgnoreCase(rule.getAppliesToClass()) ||
                    rule.getAppliesToClass().equalsIgnoreCase(coachClass.getCode());
            boolean matchesDist = distanceKm >= rule.getMinDistanceKm() && distanceKm <= rule.getMaxDistanceKm();

            if (matchesTrain && matchesClass && matchesDist) {
                baseRateKm = rule.getBaseRatePerKm();
                reservation = rule.getReservationCharge();
                service = rule.getServiceCharge();
                dynamicMult = rule.getDynamicMultiplier();
                concession = rule.getConcessionPercent();
                trace.append("Rule[").append(rule.getRuleCode()).append(" applied: baseRate=")
                        .append(baseRateKm).append("]; ");
                break;
            }
        }

        // Distance based base fare
        double calculatedBase = distanceKm * baseRateKm;
        if (calculatedBase < 150.0) calculatedBase = 150.0; // minimum floor

        // Class multiplier
        double classMultiplier = coachClass.getBaseMultiplier();
        double classAdjustedBase = calculatedBase * classMultiplier;

        // Dynamic Pricing Simulation (Academic Simulation)
        boolean simulationEnabled = isSimulationEnabled();
        double dynamicSurcharge = 0.0;
        String simNote = "Standard Static Pricing Active";

        if (simulationEnabled) {
            long daysUntilJourney = ChronoUnit.DAYS.between(LocalDate.now(), journeyDate);
            if (currentOccupancyPercent >= 80.0) {
                dynamicSurcharge = classAdjustedBase * 0.25; // +25%
                simNote = "Academic Dynamic Pricing Simulation: High Demand Tier (>80% Occupancy, +25% Dynamic Surcharge)";
            } else if (currentOccupancyPercent >= 50.0) {
                dynamicSurcharge = classAdjustedBase * 0.12; // +12%
                simNote = "Academic Dynamic Pricing Simulation: Moderate Demand Tier (50-80% Occupancy, +12% Dynamic Surcharge)";
            } else if (daysUntilJourney <= 2) {
                dynamicSurcharge = classAdjustedBase * 0.10; // +10% for last minute booking
                simNote = "Academic Dynamic Pricing Simulation: Short Lead-Time Surge (+10%)";
            } else {
                simNote = "Academic Dynamic Pricing Simulation: Normal Base Rate (<50% Occupancy)";
            }
        }

        double discountAmount = (classAdjustedBase * (concession / 100.0));
        double singlePassengerTotal = Math.round(classAdjustedBase + reservation + service + dynamicSurcharge - discountAmount);

        result.baseFare = Math.round(classAdjustedBase * 100.0) / 100.0;
        result.classCharge = Math.round((classAdjustedBase - calculatedBase) * 100.0) / 100.0;
        result.reservationCharge = reservation;
        result.serviceCharge = service;
        result.dynamicComponent = Math.round(dynamicSurcharge * 100.0) / 100.0;
        result.discount = Math.round(discountAmount * 100.0) / 100.0;
        result.finalFare = singlePassengerTotal * passengerCount;
        result.ruleAppliedTrace = trace.length() > 0 ? trace.toString() : "Default National Distance/Class Matrix Applied";
        result.simulationNote = simNote;

        return result;
    }

    public boolean isSimulationEnabled() {
        return configRepository.findByConfigKey("SIMULATION_DYNAMIC_PRICING_ENABLED")
                .map(sc -> Boolean.parseBoolean(sc.getConfigValue()))
                .orElse(true);
    }

    public void setSimulationEnabled(boolean enabled, String adminUsername) {
        SystemConfiguration config = configRepository.findByConfigKey("SIMULATION_DYNAMIC_PRICING_ENABLED")
                .orElse(new SystemConfiguration("SIMULATION_DYNAMIC_PRICING_ENABLED", "true", "Academic Dynamic Pricing Simulation Toggle"));
        config.setConfigValue(String.valueOf(enabled));
        config.setLastUpdatedBy(adminUsername);
        configRepository.save(config);
    }
}
