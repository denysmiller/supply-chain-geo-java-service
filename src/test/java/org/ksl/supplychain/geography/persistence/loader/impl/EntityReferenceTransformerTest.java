package org.ksl.supplychain.geography.persistence.loader.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;

import org.ksl.supplychain.common.model.transform.Transformable;
import org.ksl.supplychain.common.model.transform.TransformableProvider;
import org.ksl.supplychain.common.model.transform.Transformer;
import org.ksl.supplychain.common.model.transform.impl.CachedFieldProvider;
import org.ksl.supplychain.geography.model.entity.AbstractEntity;
import org.ksl.supplychain.geography.persistence.loader.EntityLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class EntityReferenceTransformerTest {

	private Transformer entityReferenceTransformer;

	@Mock
	private EntityLoader entityLoader;

	@Mock
	private TransformableProvider transformableProvider;

	@BeforeEach
	void setup() {
		entityReferenceTransformer = new EntityReferenceTransformer(entityLoader, new CachedFieldProvider(),
				transformableProvider);
		when(transformableProvider.find(any())).thenReturn(Optional.empty());
	}

	@Test
	void transform_validEntity_referenceFieldsCopied() {
		when(transformableProvider.find(any())).thenReturn((Optional)Optional.of(new SourceTransformable()));

		ParentEntity parent = new ParentEntity();
		parent.setId(1);
		SourceEntity source = new SourceEntity();
		source.parent = parent;

		SourceDTO sourceDTO = entityReferenceTransformer.transform(source, SourceDTO.class);
		assertNotNull(sourceDTO);
		assertEquals(parent.getId(), sourceDTO.parentId);
	}

	@Test
	void untransform_validDTO_referenceFieldsCopied() {
		when(transformableProvider.find(any())).thenReturn((Optional)Optional.of(new SourceTransformable()));

		SourceDTO sourceDTO = new SourceDTO();
		sourceDTO.parentId = 1;

		ParentEntity parent = new ParentEntity();
		parent.setId(1);
		when(entityLoader.load(ParentEntity.class, 1)).thenReturn(parent);

		SourceEntity source = entityReferenceTransformer.untransform(sourceDTO, SourceEntity.class);
		assertNotNull(source);
		assertEquals(parent, source.parent);
	}

	public static class SourceEntity extends AbstractEntity {
		ParentEntity parent;
	}

	public static class ParentEntity extends AbstractEntity {
	}

	public static class SourceDTO {
		int parentId;
	}
	
	private static class SourceTransformable implements Transformable<SourceEntity, SourceDTO> {

		@Override
		public Map<String, String> getSourceMapping() {
			return Map.of("parentId", "parent");
		}
		
	}
}
