package org.ksl.supplychain.geography.resource;

import java.util.List;

import org.ksl.supplychain.common.infra.exception.flow.InvalidParameterException;
import org.ksl.supplychain.common.model.transform.Transformer;
import org.ksl.supplychain.geography.dto.CityDTO;
import org.ksl.supplychain.geography.model.entity.City;
import org.ksl.supplychain.geography.resource.base.BaseResource;
import org.ksl.supplychain.geography.service.GeographicService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("cities")
/**
 * {@link CityResource} is REST web-service that handles city-related requests
 * 
 * @author Miller
 *
 */
public class CityResource extends BaseResource {
	/**
	 * Underlying source of data
	 */
	private final GeographicService service;

	/**
	 * DTO <-> Entity transformer
	 */
	private final Transformer transformer;

	@Inject
	public CityResource(GeographicService service, Transformer transformer) {
		this.transformer = transformer;

		this.service = service;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary = "Returns all the existing cities")
	/**
	 * Returns all the existing cities
	 * 
	 * @return
	 */
	public List<CityDTO> findCities() {
		return transformer.transform(service.findCities(), CityDTO.class);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Operation(summary = "Creates city object", responses = {
			@ApiResponse(responseCode = "400", description = "Invalid content of city object") })
	/**
	 * Saves new city instance
	 * 
	 * @return
	 */
	public Response save(@Valid @Parameter(name = "city", required = true) final CityDTO cityDTO) {
		City city = transformer.untransform(cityDTO, City.class);
		service.saveCity(city);

		return postForLocation(city.getId());
	}

	@PUT
	@Path("/{cityId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary = "Updates city object", responses = {
			@ApiResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON)),
			@ApiResponse(description = "Invalid content of city object", responseCode = "400") })
	/**
	 * Updates city instance
	 * 
	 * @return
	 */
	public Response update(
			@Parameter(name = "Unique numeric city identifier", required = true, example = "123", in = ParameterIn.PATH) @PathParam("cityId") final int cityId,
			@Valid @RequestBody(required = true, description = "Updated city object", content = @Content(mediaType = MediaType.APPLICATION_JSON)) final CityDTO cityDTO) {
		City city = transformer.untransform(cityDTO, City.class);
		service.saveCity(city);

		return ok(transformer.transform(city, CityDTO.class));
	}

	@DELETE
	@Path("/{cityId}")
	@Operation(summary = "Updates city object", responses = {
			@ApiResponse(responseCode = "400", description = "Invalid content of city object"),
			@ApiResponse(responseCode = "404", description = "Identifier of the non-existing city") })
	/**
	 * Deletes city instance
	 * 
	 */
	public void delete(
			@Parameter(name = "Unique numeric city identifier", required = true) @PathParam("cityId") final int cityId) {
		service.deleteCity(cityId);
	}

	@Path("/{cityId}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary = "Returns existing city by its identifier", responses = {
			@ApiResponse(responseCode = "400", description = "Invalid city identifier"),
			@ApiResponse(responseCode = "404", description = "Identifier of the non-existing city") })
	/**
	 * Returns city with specified identifier
	 * 
	 * @return
	 */
	public Response findById(
			@Parameter(name = "Unique numeric city identifier", required = true) @PathParam("cityId") final int cityId) {
		City city = service.findCityById(cityId)
				.orElseThrow(() -> new InvalidParameterException("Invalid city identifier: " + cityId));

		return ok(transformer.transform(city, CityDTO.class));
	}

}
