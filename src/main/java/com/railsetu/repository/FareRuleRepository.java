package com.railsetu.repository;

import com.railsetu.domain.FareRule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FareRuleRepository extends JpaRepository<FareRule, Long> {
    Optional<FareRule> findByRuleCode(String ruleCode);
    List<FareRule> findByActiveTrueOrderByPriorityAsc();
}
