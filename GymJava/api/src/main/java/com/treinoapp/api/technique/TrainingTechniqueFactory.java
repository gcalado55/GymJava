package com.treinoapp.api.technique;


public class TrainingTechniqueFactory {
    public static TrainingTechnique fromCode(String code){
        if(code == null){
            return new NoTechnique();
        }
        return switch (code.toUpperCase()){
            case "DROP_SET" -> new DropSetTechnique();
            case "CLUSTER_SET" -> new ClusterSetTechnique();
            default -> new NoTechnique();
        };
    }
}
