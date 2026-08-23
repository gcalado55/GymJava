package com.treinoapp.api.technique;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class TrainingTechniqueFactoryTest {

    @Test
    void shouldReturnDropSetTechniqueWhenCodeIsDropSet() {
        String code = "DROP_SET";

        TrainingTechnique result = TrainingTechniqueFactory.fromCode(code);

        assertInstanceOf(DropSetTechnique.class, result);
        assertEquals("Drop Set", result.getDisplayName());
    }

    @Test
    void shouldReturnClusterSetTechniqueWhenCodeIsClusterSet() {
        String code = "CLUSTER_SET";

        TrainingTechnique result = TrainingTechniqueFactory.fromCode(code);

        assertInstanceOf(ClusterSetTechnique.class, result);
        assertEquals("Cluster Set", result.getDisplayName());
    }

    @Test
    void shouldReturnNoTechniqueWhenCodeIsUnknown() {
        String code = "XYZ";

        TrainingTechnique result = TrainingTechniqueFactory.fromCode(code);

        assertInstanceOf(NoTechnique.class, result);
        assertEquals("No Technique", result.getDisplayName());
    }

    @Test
    void shouldReturnNoTechniqueWhenCodeIsNull() {
        String code = null;

        TrainingTechnique result = TrainingTechniqueFactory.fromCode(code);

        assertInstanceOf(NoTechnique.class, result);
        assertEquals("No Technique", result.getDisplayName());
    }

}