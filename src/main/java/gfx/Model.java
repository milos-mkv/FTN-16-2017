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

        int numMaterials = scene.mNumMaterials() - 1;
        System.out.println(numMaterials);
        PointerBuffer aiMaterials = scene.mMaterials();
        materials = new ArrayList<>();
        for (int i = 0; i < numMaterials; i++) {
            AIMaterial aiMaterial = AIMaterial.create(Objects.requireNonNull(aiMaterials).get(i));
            processMaterial(aiMaterial, resourcePath.substring(0, resourcePath.lastIndexOf("/")));
        }

        processNode(Objects.requireNonNull(scene.mRootNode()), scene);
    }

    private Texture getMaterialTexture(AIMaterial material, String texturesDir, int type) {
        AIString buffer = AIString.calloc();
        Assimp.aiGetMaterialTexture(material, type, 0, buffer, (IntBuffer) null, null, null, null, null, null);
        if (!buffer.dataString().equals("")) {
            return TextureCache.getTexture(texturesDir + "/" + buffer.dataString());
        }
        return null;
    }

    private Vector4f getMaterialColor(AIMaterial material, String type) {
        AIColor4D color = AIColor4D.create();
        int result = Assimp.aiGetMaterialColor(material, type, Assimp.aiTextureType_NONE, 0, color);
        if (result == 0) {
            return new Vector4f(color.r(), color.g(), color.b(), color.a());
        }
        return Material.DEFAULT_COLOR;
    }

    public void processMaterial(AIMaterial aiMaterial, String texturesDir) throws RuntimeException {
        Texture diffuseTexture = getMaterialTexture(aiMaterial, texturesDir, Assimp.aiTextureType_DIFFUSE);
        Texture specularTexture = getMaterialTexture(aiMaterial, texturesDir, Assimp.aiTextureType_SPECULAR);
        Texture normalsTexture = getMaterialTexture(aiMaterial, texturesDir, Assimp.aiTextureType_NORMALS);

        Vector4f ambient = getMaterialColor(aiMaterial, Assimp.AI_MATKEY_COLOR_AMBIENT);
        Vector4f diffuse = getMaterialColor(aiMaterial, Assimp.AI_MATKEY_COLOR_DIFFUSE);
        Vector4f specular = getMaterialColor(aiMaterial, Assimp.AI_MATKEY_COLOR_SPECULAR);


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