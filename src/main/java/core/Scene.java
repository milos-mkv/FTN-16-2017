package core;

import gfx.Mesh;
import gfx.Model;
import gfx.TransformComponent;

import java.util.ArrayList;

public abstract class Scene {

    public static ArrayList<Model> models = new ArrayList<>();

    public static TransformComponent selected;

    public static void loadModelFromFile(String file) {
        models.add(new Model(file));
    }


}
