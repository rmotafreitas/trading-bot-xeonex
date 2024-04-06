package xeonex.xeonex.domain.User;

import jakarta.persistence.Embeddable;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class Risk {

    private Integer riskLevel;

    public Risk(Integer riskLevel) {
        if (riskLevel < 5 || riskLevel > 80) {
            throw new IllegalArgumentException("Risk level must be between 0 and 10");
        }
        this.riskLevel = riskLevel;
    }
}
