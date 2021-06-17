package gfx;


import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.assimp.*;

import java.util.ArrayList;
import java.util.Objects;

public class Model {

    public ArrayList<Mesh> meshes = new ArrayList<>();

    public Model(String resourcePath) {
        AIScene scene = Assimp.aiImportFile(resourcePath, Assimp.aiProcess_Triangulate | Assimp.aiProcess_FlipUVs
                | Assimp.aiProcess_GenSmoothNormals | Assimp.aiProcess_CalcTangentSpace);
        if (scene == null) {
            throw new RuntimeException("Failed to load model");
        }

        processNode(Objects.requireNonNull(scene.mRootNode()), scene);
    }

    public void processNode(AINode node, AIScene scene) {
        for (int i = 0; i < node.mNumMeshes(); i++) {
            AIMesh mesh = AIMesh.create(Objects.requireNonNull(scene.mMeshes()).get(Objects.requireNonNull(node.mMeshes()).get(i)));
            meshes.add(processMesh(mesh, scene));
        }
        for( int i = 0; i < node.mNumChildren(); i++) {
            processNode(AINode.create(Objects.requireNonNull(node.mChildren()).get(i)), scene);
        }
    }

    public void draw(Shader shader) {
        System.out.println(meshes.size());
        for(int i=0;i<meshes.size();i++) {
            meshes.get(i).draw(shader);
        }
    }

    public Mesh processMesh(AIMesh mesh, AIScene scene) {
        ArrayList<Vertex> vertices = new ArrayList<>();
        ArrayList<Integer> indices = new ArrayList<>();
        ArrayList<Texture> textures = new ArrayList<>();

        for (int i = 0; i < mesh.mNumVertices(); i++) {
            Vertex vertex = new Vertex();
            vertex.position = new Vector3f(mesh.mVertices().get(i).x(), mesh.mVertices().get(i).y(), mesh.mVertices().get(i).z());

            if (mesh.mNormals() != null) {
                vertex.normal = new Vector3f(mesh.mNormals().get(i).x(), mesh.mNormals().get(i).y(), mesh.mNormals().get(i).z());
            }

            if (mesh.mTextureCoords().get(0) != 0) {
                vertex.texCoords = new Vector2f(mesh.mTextureCoords(0).get(i).x(),
                        mesh.mTextureCoords(0).get(i).y());
            } else {
                vertex.texCoords = new Vector2f(0, 0);
            }
            vertices.add(vertex);
        }

        for (int i = 0; i < mesh.mNumFaces(); i++) {
            AIFace face = mesh.mFaces().get(i);
            for (int j = 0; j < face.mNumIndices(); j++) {
                indices.add(face.mIndices().get(j));
            }
        }

        return new Mesh(vertices, indices, textures);
    }

}