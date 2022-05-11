/**
 * @author Youngjae Lee
 * @version 2022-03-07
 *
 * description: Property assessment resource
 */

package ca.nait.dmit.dmit2015.youngjaelee.assignment04.resource;

import ca.nait.dmit.dmit2015.youngjaelee.assignment04.entity.EdmontonPropertyAssessment;
import ca.nait.dmit.dmit2015.youngjaelee.assignment04.repository.EdmontonPropertyAssessmentRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@ApplicationScoped
@Path("EdmontonPropertyAssessments")                    // All methods of this class are associated this URL path
@Consumes(MediaType.APPLICATION_JSON)    // All methods this class accept only JSON format data
@Produces(MediaType.APPLICATION_JSON)
public class EdmontonPropertyAssessmentResource {

    @Inject
    private EdmontonPropertyAssessmentRepository _edmontonPropertyAssessmentRepository;

    @GET
    @Path("count")
    public Response count() {
        return Response.ok(_edmontonPropertyAssessmentRepository.count()).build();
    }

    @GET
    @Path("/query/within")
    public Response within(
            @QueryParam("longitude") double longitude,
            @QueryParam("latitude") double latitude,
            @QueryParam("distance") double distanceMetre
    ) {
        List<EdmontonPropertyAssessment> queryResultList = _edmontonPropertyAssessmentRepository
                .findWithinDistance(longitude, latitude, distanceMetre);
        return Response.ok(queryResultList).header("Count",queryResultList.size()).build();
    }

    @GET
    @Path("/query/byHouseNumberAndStreetName")
    public Response findByHouseNumberAndStreetName(
            @QueryParam("houseNumber") String houseNumber,
            @QueryParam("streetName") String streetName) {
        EdmontonPropertyAssessment querySingleResult = _edmontonPropertyAssessmentRepository
                .findByHouseNumberAndStreetName(houseNumber, streetName)
                .orElseThrow(NotFoundException::new);
        return Response.ok(querySingleResult).build();
    }

    @GET
    @Path("/query/byNeighbourhood")
    public Response findByNeighbourhood(
            @QueryParam("neighbourhood") String neighbourhood
    ) {
        List<EdmontonPropertyAssessment> queryResultList = _edmontonPropertyAssessmentRepository
                .findByNeighbourhood(neighbourhood);
        return Response.ok(queryResultList).header("Count",queryResultList.size()).build();
    }

    @GET
    @Path("/query/byNeighbourhoodAndAssessedValueRange")
    public Response findByNeighbourhoodAndAssessedValue(
            @QueryParam("neighbourhood") String neighbourhood,
            @QueryParam("minimumAssessedValue") Integer minimumAssessedValue,
            @QueryParam("maximumAssessedValue") Integer maximumAssessedValue
    ) {
        List<EdmontonPropertyAssessment> queryResultList = _edmontonPropertyAssessmentRepository
                .findByNeighbourhoodAndAssessedValue(neighbourhood,minimumAssessedValue,maximumAssessedValue);
        return Response.ok(queryResultList).header("Count",queryResultList.size()).build();
    }

    @GET
    @Path("/query/distinctWards")
    public Response findDistinctWard(
    ) {
        List<String> queryResultList = _edmontonPropertyAssessmentRepository
                .findDistinctWard();
        return Response.ok(queryResultList).header("Count",queryResultList.size()).build();
    }

    @GET
    @Path("/query/neighbourhoodMap")
    public Response findByNeighbourhoodAndNeighbourhoodId(
    ) {

        JsonArray queryResultList = _edmontonPropertyAssessmentRepository.neighbourhoodMap();

        return Response.ok(queryResultList).header("Count",queryResultList.size()).build();
    }
}
