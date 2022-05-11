/**
 * @author Youngjae Lee
 * @version 2022-03-07
 *
 * description: Property assessment processor
 */

package ca.nait.dmit.dmit2015.youngjaelee.assignment04.batch;

import ca.nait.dmit.dmit2015.youngjaelee.assignment04.entity.EdmontonPropertyAssessment;
import jakarta.batch.api.chunk.ItemProcessor;
import jakarta.inject.Named;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

@Named
public class EdmontonPropertyAssessmentProcessor implements ItemProcessor {

    @Override
    public Object processItem(Object item) throws Exception {
        String line = (String) item;
        final String delimiter = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
        String[] tokens = line.split(delimiter, -1);

        EdmontonPropertyAssessment currentProperty = new EdmontonPropertyAssessment();
        currentProperty.setAccountNumber(tokens[0]);
        currentProperty.setSuite(tokens[1].trim().equals("") ? null : tokens[1]);
        currentProperty.setHouseNumber(tokens[2]);
        currentProperty.setStreetName(tokens[3]);
        currentProperty.setGarage(tokens[4].trim().equals("Y"));
        currentProperty.setNeighbourhoodId(tokens[5].trim().equals("") ? null : Integer.parseInt(tokens[5]));
        currentProperty.setNeighbourhood(tokens[6]);
        currentProperty.setWard(tokens[7]);
        currentProperty.setAssessedValue(Integer.parseInt(tokens[8]));
        currentProperty.setLatitude(Double.parseDouble(tokens[9]));
        currentProperty.setLongitude(Double.parseDouble(tokens[10]));
        Point geoLocation = new GeometryFactory().createPoint(
                new Coordinate(
                        currentProperty.getLongitude(), currentProperty.getLatitude()
                )
        );
        currentProperty.setPointLocation(geoLocation);
        currentProperty.setAssessmentClass1(tokens[12]);

        return currentProperty;
    }
}
