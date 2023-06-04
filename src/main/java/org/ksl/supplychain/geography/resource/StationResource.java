package org.ksl.supplychain.geography.resource;

import java.util.List;

import org.ksl.supplychain.common.infra.exception.flow.InvalidParameterException;
import org.ksl.supplychain.common.model.search.criteria.range.RangeCriteria;
import org.ksl.supplychain.common.model.transform.Transformer;
import org.ksl.supplychain.geography.dto.StationDTO;
import org.ksl.supplychain.geography.model.entity.Station;
import org.ksl.supplychain.geography.model.search.criteria.StationCriteria;
import org.ksl.supplychain.geography.resource.base.BaseResource;
import org.ksl.supplychain.geography.service.GeographicService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("stations")
/**
 * {@link StationResource} is REST web-service that handles station-related
 * requests
 * 
 * @author Miller
 *
 */
public class StationResource extends BaseResource {
	/**
	 * Underlying source of data
	 */
	private final GeographicService service;

	/**
	 * DTO <-> Entity transformer
	 */
	private final Transformer transformer;

	@Inject
	public StationResource(GeographicService service, Transformer transformer) {
		this.transformer = transformer;

		this.service = service;
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Operation(summary = "Saves station object", responses = {
			@ApiResponse(responseCode = "400", description = "Invalid content of station object") })
	/**
	 * Saves station instance
	 * 
	 * @return
	 */
	public Response save(
			@Valid @RequestBody(description = "Saved station object", required = true) StationDTO stationDTO) {
		Station station = transformer.untransform(stationDTO, Station.class);
		service.saveStation(station);

		return postForLocation(station.getId());
	}

	@Path("/{stationId}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary = "Returns existing station by its identifier", responses = {
			@ApiResponse(responseCode = "400", description = "Invalid station identifier"),
			@ApiResponse(responseCode = "404", description = "Identifier of the non-existing station") })
	/**
	 * Returns station with specified identifier
	 * 
	 * @return
	 */
	public Response findById(
			@Parameter(name = "Unique numeric station identifier", required = true) @PathParam("stationId") final int stationId) {
		Station station = service.findStationById(stationId)
				.orElseThrow(() -> new InvalidParameterException("Invalid route identifier: " + stationId));

		return ok(transformer.transform(station, StationDTO.class));
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary = "Returns all the existing stations")
	/**
	 * Returns all the existing stations
	 * 
	 * @return
	 */
	public List<StationDTO> findAll() {
		List<Station> stations = service.searchStations(new StationCriteria(), new RangeCriteria(0, Integer.MAX_VALUE));

		return transformer.transform(stations, StationDTO.class);
	}

}
