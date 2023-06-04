package org.ksl.supplychain.geography.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.apache.commons.lang3.math.NumberUtils;
import org.ksl.supplychain.geography.dto.CityDTO;
import org.ksl.supplychain.geography.jersey.extension.JerseyTestExtension;
import org.ksl.supplychain.geography.resource.config.JerseyConfig;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * {@link CityResourceTest} is integration test that verifies
 * {@link CityResource}
 * 
 * @author Miller
 *
 */
public class CityResourceTest {

	@RegisterExtension
	JerseyTestExtension extension = new JerseyTestExtension(configure());

	private Application configure() {
		return new JerseyConfig();
	}

	@Test
	@Disabled
	void testFindCitiesSuccess(WebTarget target) {
		List<Map<String, String>> cities = target.path("cities").request().get(List.class);
		assertNotNull(cities);
		assertTrue(cities.isEmpty());
	}

	@Test
	@Disabled
	void testFindCityByIdSuccess(WebTarget target) {
		CityDTO city = target.path("cities/1").request().get(CityDTO.class);
		assertNotNull(city);
		assertEquals(1, city.getId());
		assertEquals("Odessa", city.getName());
	}

	@Test
	void testFindCityByIdNotFound(WebTarget target) {
		Response response = target.path("cities/20000").request().get(Response.class);
		assertNotNull(response);
		assertEquals(response.getStatus(), Response.Status.NOT_FOUND.getStatusCode());
	}

	@Test
	void testFindCityByIdInvalidId(WebTarget target) {
		Response response = target.path("cities/aaab").request().get(Response.class);
		assertNotNull(response);
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
	}

	@Test
	void save_emptyName_badRequestReturned(WebTarget target) {
		CityDTO city = new CityDTO();
		city.setDistrict("Nikolaev");
		city.setRegion("Nikolaev");
		Response response = target.path("cities").request().post(Entity.entity(city, MediaType.APPLICATION_JSON_TYPE));
		assertNotNull(response);
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
	}

	@Test
	void save_cityValid_successStatusReturned(WebTarget target) {
		CityDTO city = new CityDTO();
		city.setName("Odessa");
		city.setDistrict("Odessa");
		city.setRegion("Odessa");
		Response response = target.path("cities").request().post(Entity.entity(city, MediaType.APPLICATION_JSON_TYPE));
		assertNotNull(response);
		assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
		assertNotNull(response.getHeaderString(HttpHeaders.LOCATION));
	}

	@Test
	void save_nameTooShort_badRequestReturned(WebTarget target) {
		CityDTO city = new CityDTO();
		city.setName("N");
		city.setDistrict("Odessa");
		city.setRegion("Odessa");
		Response response = target.path("cities").request().post(Entity.entity(city, MediaType.APPLICATION_JSON_TYPE));
		assertNotNull(response);
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
	}

	@Test
	void save_nameTooLong_badRequestReturned(WebTarget target) {
		CityDTO city = new CityDTO();
		city.setName("N1234567890123456789012345678901234567890");
		city.setDistrict("Odessa");
		city.setRegion("Odessa");
		Response response = target.path("cities").request().post(Entity.entity(city, MediaType.APPLICATION_JSON_TYPE));
		assertNotNull(response);
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
	}

	@Test
	void update_validCity_cityUpdated(WebTarget target) {
		CityDTO city = new CityDTO();
		city.setName("Lviv");
		city.setDistrict("Lviv");
		city.setRegion("Lviv");

		Response response = target.path("cities").request().post(Entity.entity(city, MediaType.APPLICATION_JSON_TYPE));
		assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
		int cityId = NumberUtils.toInt(response.getHeaderString("Location"));
		city.setId(cityId);
		city.setName("Lviw");
		CityDTO updatedCity = target.path("cities/" + cityId).request()
				.put(Entity.entity(city, MediaType.APPLICATION_JSON_TYPE), CityDTO.class);
		assertEquals(city.getName(), updatedCity.getName());
	}
	
	@Test
	void delete_validCity_cityDeleted(WebTarget target) {
		CityDTO city = new CityDTO();
		city.setName("Test");
		city.setDistrict("Test");
		city.setRegion("Test");

		Response response = target.path("cities").request().post(Entity.entity(city, MediaType.APPLICATION_JSON_TYPE));
		int cityId = NumberUtils.toInt(response.getHeaderString("Location"));
		Response deleteResponse = target.path("cities/" + cityId).request()
				.delete();
		assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
	}

	@SuppressWarnings("unchecked")
	@Test
	void testSaveCitySuccess(WebTarget target) throws Throwable {
		CityDTO city = new CityDTO();
		city.setName("Kiev");
		city.setDistrict("Kiev");
		city.setRegion("Kiev");

		CompletableFuture<Void> cf = target.path("cities").request().rx()
				.post(Entity.entity(city, MediaType.APPLICATION_JSON))
				.thenAccept(response -> assertEquals(response.getStatus(), Response.Status.CREATED.getStatusCode()))
				.thenCompose(v -> target.path("cities").request().rx().get(Response.class)).thenAccept(response -> {
					List<Map<String, String>> cities = (List<Map<String, String>>) response.readEntity(List.class);
					assertNotNull(cities);
					assertTrue(cities.stream().anyMatch(item -> item.get("name").equals("Kiev")));
				}).toCompletableFuture();

		try {
			cf.join();
		} catch (CompletionException e) {
			if (e.getCause() != null) {
				throw e.getCause();
			}
			fail(e.getMessage());
		}
	}

}
