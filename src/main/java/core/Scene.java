package core;

import gfx.Mesh;
import gfx.Model;
import gfx.PointLight;
import gfx.TransformComponent;

import java.util.ArrayList;
import java.util.List;

public abstract class Scene {

    public static ArrayList<Model> models = new ArrayList<>();

    public static List<PointLight> pointLights = new ArrayList<>();

    public static Mesh selected;

    public static void loadModelFromFile(String file) {
        models.add(new Model(file));
    }


    public static void init() {

    }


}
