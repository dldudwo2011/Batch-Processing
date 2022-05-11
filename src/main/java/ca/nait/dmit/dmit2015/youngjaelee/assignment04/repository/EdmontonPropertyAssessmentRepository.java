/**
 * @author Youngjae Lee
 * @version 2022-03-07
 *
 * description: Property assessment repository
 */

package ca.nait.dmit.dmit2015.youngjaelee.assignment04.repository;

import ca.nait.dmit.dmit2015.youngjaelee.assignment04.entity.EdmontonPropertyAssessment;
import common.jpa.AbstractJpaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.spi.JsonProvider;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Transactional
public class EdmontonPropertyAssessmentRepository extends AbstractJpaRepository<EdmontonPropertyAssessment, String> {

    public EdmontonPropertyAssessmentRepository() {
        super(EdmontonPropertyAssessment.class);
    }

    public List<EdmontonPropertyAssessment> findWithinDistance(
            double longitude,
            double latitude,
            double distanceMeters
    ) {
        List<EdmontonPropertyAssessment> propertyList = new ArrayList<>();

        var distanceKm = distanceMeters / 1000;
        final double EARTH_MEAN_RADIUS_KM = 6371.0087714;
        final double DEGREES_TO_RADIANS =  Math.PI / 180;
        final double RADIANS_TO_DEGREES =  1 / DEGREES_TO_RADIANS;
        var distanceCentralAngleDegrees = distanceKm * RADIANS_TO_DEGREES / EARTH_MEAN_RADIUS_KM;

        final String jpql = """
        SELECT p
        FROM EdmontonPropertyAssessment p
        WHERE distance(p.pointLocation, :pointValue) <= :distanceCentralAngleDegreesValue
        """;
        TypedQuery<EdmontonPropertyAssessment> query = getEntityManager().createQuery(jpql, EdmontonPropertyAssessment.class);

        Point geoLocation = new GeometryFactory()
                .createPoint(
                        new Coordinate( longitude, latitude  )
                );

        query.setParameter("pointValue", geoLocation);
        query.setParameter("distanceCentralAngleDegreesValue", distanceCentralAngleDegrees);
        propertyList = query
                .setMaxResults(25)
                .getResultList();

        return propertyList;
    }


    public Optional<EdmontonPropertyAssessment> findByHouseNumberAndStreetName(
            String houseNumber,
            String streetName
    ) {
        Optional<EdmontonPropertyAssessment> optionalSingleResult = Optional.empty();

        List<EdmontonPropertyAssessment> queryResultList = getEntityManager()
                .createQuery("""
                        SELECT p
                        FROM EdmontonPropertyAssessment p
                        WHERE p.houseNumber = :houseNumberValue AND p.streetName = :streetNameValue
                        """)
                .setParameter("houseNumberValue", houseNumber)
                .setParameter("streetNameValue", streetName)
                .getResultList();
        if (queryResultList.size() > 0) {
            optionalSingleResult = Optional.of(queryResultList.get(0));
        }

        return optionalSingleResult;
    }

    public List<EdmontonPropertyAssessment> findByNeighbourhood(
            String neighbourhood
    ) {
        List<EdmontonPropertyAssessment> queryResultList = getEntityManager()
                .createQuery("""
                        SELECT p
                        FROM EdmontonPropertyAssessment p
                        WHERE p.neighbourhood = :neighbourhoodValue 
                        """)
                .setParameter("neighbourhoodValue", neighbourhood)
                .setMaxResults(999)
                .getResultList();

        return queryResultList;
    }

    public List<EdmontonPropertyAssessment> findByNeighbourhoodAndAssessedValue(
            String neighbourhood,
            Integer minimumAssessedValue,
            Integer maximumAssessedValue
    ) {
        List<EdmontonPropertyAssessment> queryResultList = getEntityManager()
                .createQuery("""
                        SELECT p
                        FROM EdmontonPropertyAssessment p
                        WHERE p.neighbourhood = :neighbourhoodValue AND p.assessedValue >= :minimumAssessedVal AND p.assessedValue <= :maximumAssessedVal
                        """)
                .setParameter("neighbourhoodValue", neighbourhood)
                .setParameter("minimumAssessedVal", minimumAssessedValue)
                .setParameter("maximumAssessedVal", maximumAssessedValue)
                .setMaxResults(99)
                .getResultList();

        return queryResultList;
    }

    public List<String> findDistinctWard(
    ) {

        List<String> queryResultList = getEntityManager()
                .createQuery("""
                        SELECT DISTINCT ward
                        FROM EdmontonPropertyAssessment
                        WHERE ward != ''
                        order by ward
                        """)
                .getResultList();
        return queryResultList;
    }

    public JsonArray neighbourhoodMap(
    ) {

        List<Tuple> resultList = getEntityManager().createQuery(
                        "SELECT DISTINCT p.neighbourhoodId as NeighbourhoodID, p.neighbourhood as Neighbourhood FROM EdmontonPropertyAssessment p WHERE p.neighbourhoodId != null AND p.neighbourhood != null", Tuple.class).getResultList();

        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        resultList.forEach(tuple -> {
            JsonObject jsonObject = Json.createObjectBuilder()
                    .add("neighbourhoodId", tuple.get("NeighbourhoodID", Integer.class))
                    .add("neighbourhood", tuple.get("Neighbourhood", String.class))
                    .build();
            jsonArrayBuilder.add(jsonObject);
        });

        return jsonArrayBuilder.build();
    }
}
