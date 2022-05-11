/**
 * @author Youngjae Lee
 * @version 2022-03-07
 *
 * description: Property assessment entity
 */

package ca.nait.dmit.dmit2015.youngjaelee.assignment04.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "ylee39EdmontonPropertyAssessment")
@Data
@Getter
@Setter
public class EdmontonPropertyAssessment implements Serializable {
        @Id
        public String accountNumber;
        public String suite;
        public String houseNumber;
        public String streetName;
        public Boolean garage;
        public Integer neighbourhoodId;
        public String neighbourhood;
        public String ward;
        public Integer assessedValue;
        public Double latitude;
        public Double longitude;
        public String assessmentClass1;

        @Column(name = "point_location")
        @jakarta.json.bind.annotation.JsonbTransient
        private org.locationtech.jts.geom.Point pointLocation;

        private LocalDateTime createdDateTime;

    public EdmontonPropertyAssessment() {
    }

        @PrePersist
        private void beforePersist() {
                createdDateTime = LocalDateTime.now();
        }
}
