package gfx;


import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Model extends TransformComponent {

    public ArrayList<Mesh> meshes = new ArrayList<>();
    public List<Material> materials = new ArrayList<>();

    public Matrix4f getTransform() {
        return new Matrix4f().translate(position).rotate(rotation.x, 1, 0, 0)
                .rotate(rotation.y, 0, 1, 0).rotate(rotation.z, 0, 0, 1).scale(scale);
    }

    public Model(String resourcePath) {
        super();
        AIScene scene = Assimp.aiImportFile(resourcePath, Assimp.aiProcess_Triangulate | Assimp.aiProcess_FlipUVs
                | Assimp.aiProcess_GenSmoothNormals | Assimp.aiProcess_CalcTangentSpace);
        if (scene == null) {
            throw new RuntimeException("Failed to load model");
        }

        int numMaterials = scene.mNumMaterials();
        System.out.println(numMaterials);
        PointerBuffer aiMaterials = scene.mMaterials();
        materials = new ArrayList<>();
        for (int i = 0; i < numMaterials; i++) {
            AIMaterial aiMaterial = AIMaterial.create(Objects.requireNonNull(aiMaterials).get(i));
            processMaterial(aiMaterial, resourcePath.substring(0, resourcePath.lastIndexOf("/")));
        }

        processNode(Objects.requireNonNull(scene.mRootNode()), scene);
    }

    public void processMaterial(AIMaterial aiMaterial, String texturesDir) throws RuntimeException {
        AIColor4D colour = AIColor4D.create();

        AIString path = AIString.calloc();

        Assimp.aiGetMaterialTexture(aiMaterial, Assimp.aiTextureType_DIFFUSE, 0, path, (IntBuffer) null, null, null, null, null, null);
        String diffuseTexturePath = texturesDir + "/" + path.dataString();
        System.out.println(path.dataString());
        Texture diffuseTexture = null;
        if (!path.dataString().equals("")) {
            diffuseTexture = TextureCache.getTexture(diffuseTexturePath);
        }

        Assimp.aiGetMaterialTexture(aiMaterial, Assimp.aiTextureType_SPECULAR, 0, path, (IntBuffer) null, null, null, null, null, null);
        String specularTexturePath = texturesDir + "/" + path.dataString();
        Texture specularTexture = null;
        if (!path.dataString().equals("")) {
            specularTexture = TextureCache.getTexture(specularTexturePath);
        }

        Assimp.aiGetMaterialTexture(aiMaterial, Assimp.aiTextureType_NORMALS, 0, path, (IntBuffer) null, null, null, null, null, null);
        String normalsTexturePath = texturesDir + "/" + path.dataString();
        Texture normalsTexture = null;
        if (!path.dataString().equals("")) {
            normalsTexture = TextureCache.getTexture(normalsTexturePath);
        }

        Vector4f ambient = Material.DEFAULT_COLOR;
        int result = Assimp.aiGetMaterialColor(aiMaterial, Assimp.AI_MATKEY_COLOR_AMBIENT, Assimp.aiTextureType_NONE, 0, colour);
        if (result == 0) {
            ambient = new Vector4f(colour.r(), colour.g(), colour.b(), colour.a());
        }

        Vector4f diffuse = Material.DEFAULT_COLOR;
        result = Assimp.aiGetMaterialColor(aiMaterial, Assimp.AI_MATKEY_COLOR_DIFFUSE, Assimp.aiTextureType_NONE, 0, colour);
        if (result == 0) {
            diffuse = new Vector4f(colour.r(), colour.g(), colour.b(), colour.a());
        }

        Vector4f specular = Material.DEFAULT_COLOR;
        result = Assimp.aiGetMaterialColor(aiMaterial, Assimp.AI_MATKEY_COLOR_SPECULAR, Assimp.aiTextureType_NONE, 0, colour);
        if (result == 0) {
            specular = new Vector4f(colour.r(), colour.g(), colour.b(), colour.a());
        }

        Material material = new Material(ambient, diffuse, specular, 1.0f);
        material.diffuseTexture = diffuseTexture;
        material.specularTexture = specularTexture;
        material.normalTexture = normalsTexture;
        materials.add(material);
    }

    public void processNode(AINode node, AIScene scene) {
        for (int i = 0; i < node.mNumMeshes(); i++) {
            AIMesh mesh = AIMesh.create(Objects.requireNonNull(scene.mMeshes()).get(Objects.requireNonNull(node.mMeshes()).get(i)));
            meshes.add(processMesh(mesh, scene));
        }
        for (int i = 0; i < node.mNumChildren(); i++) {
            processNode(AINode.create(Objects.requireNonNull(node.mChildren()).get(i)), scene);
        }
    }

    public void draw(Shader shader) {
        for (int i = 0; i < meshes.size(); i++) {
            meshes.get(i).draw(shader);
        }
    }

    public Mesh processMesh(AIMesh mesh, AIScene scene) {
        ArrayList<Vertex> vertices = new ArrayList<>();
        ArrayList<Integer> indices = new ArrayList<>();
        ArrayList<Material> materials = new ArrayList<>();

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

        Material material;
        int materialIdx = mesh.mMaterialIndex();
        if (materialIdx >= 0 && materialIdx < materials.size()) {
            material = materials.get(materialIdx);
        } else {
            material = new Material();
        }

        return new Mesh(StandardCharsets.UTF_8.decode(mesh.mName().data()).toString(), vertices, indices, material);
    }

}