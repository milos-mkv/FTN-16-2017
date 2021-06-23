package gfx;

import core.Constants;
import lombok.Getter;
import lombok.SneakyThrows;
import managers.TextureManager;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

public class Model extends TransformComponent implements Cloneable {

    @Getter
    private final ArrayList<Mesh> meshes = new ArrayList<>();

    @Getter
    private final ArrayList<Material> materials = new ArrayList<>();

    public Model(String resourcePath) throws RuntimeException {
        super();
        AIScene scene = Assimp.aiImportFile(resourcePath, Assimp.aiProcess_Triangulate | Assimp.aiProcess_FlipUVs
                | Assimp.aiProcess_GenSmoothNormals | Assimp.aiProcess_CalcTangentSpace);
        if (scene == null) {
            throw new RuntimeException("Failed to load model");
        }

        int numMaterials = scene.mNumMaterials();
        System.out.println(numMaterials);
        PointerBuffer aiMaterials = scene.mMaterials();
        for (int i = 0; i < numMaterials; i++) {
            AIMaterial aiMaterial = AIMaterial.create(Objects.requireNonNull(aiMaterials).get(i));
            processMaterial(aiMaterial, resourcePath.substring(0, resourcePath.lastIndexOf("/")));
        }

        processNode(Objects.requireNonNull(scene.mRootNode()), scene);
    }

    public Matrix4f getTransform() {
        return new Matrix4f().translate(position).rotate(rotation.x, 1, 0, 0)
                .rotate(rotation.y, 0, 1, 0).rotate(rotation.z, 0, 0, 1).scale(scale);
    }

    public void draw(Shader shader) {
        shader.setUniformMat4("model", getTransform());

        for (int i = 0; i < meshes.size(); i++) {
            meshes.get(i).draw(shader);
        }
    }

    private Texture getMaterialTexture(AIMaterial material, String texturesDir, int type) {
        AIString buffer = AIString.calloc();
        Assimp.aiGetMaterialTexture(material, type, 0, buffer, (IntBuffer) null, null, null, null, null, null);
        if (!buffer.dataString().equals("")) {
            return TextureManager.getTexture(texturesDir + "/" + buffer.dataString());
        }
        return null;
    }

    private Vector3f getMaterialColor(AIMaterial material, String type) {
        AIColor4D color = AIColor4D.create();
        int result = Assimp.aiGetMaterialColor(material, type, Assimp.aiTextureType_NONE, 0, color);
        if (result == 0) {
            return new Vector3f(color.r(), color.g(), color.b());
        }
        return Constants.DEFAULT_COLOR;
    }

    @SneakyThrows
    private void processMaterial(AIMaterial aiMaterial, String texturesDir) throws RuntimeException {
        Texture diffuseTexture = getMaterialTexture(aiMaterial, texturesDir, Assimp.aiTextureType_DIFFUSE);
        Texture specularTexture = getMaterialTexture(aiMaterial, texturesDir, Assimp.aiTextureType_SPECULAR);
        Texture normalsTexture = getMaterialTexture(aiMaterial, texturesDir, Assimp.aiTextureType_NORMALS);

        Vector3f ambient = getMaterialColor(aiMaterial, Assimp.AI_MATKEY_COLOR_AMBIENT);
        Vector3f diffuse = getMaterialColor(aiMaterial, Assimp.AI_MATKEY_COLOR_DIFFUSE);
        Vector3f specular = getMaterialColor(aiMaterial, Assimp.AI_MATKEY_COLOR_SPECULAR);

        Material material = new Material((Vector3f)ambient.clone(), (Vector3f)diffuse.clone(),(Vector3f) specular.clone(), 1.0f);
        material.diffuseTexture  = diffuseTexture;
        material.specularTexture = specularTexture;
        material.normalTexture   = normalsTexture;
        materials.add(material);
    }

    private void processNode(AINode node, AIScene scene) {
        for (int i = 0; i < node.mNumMeshes(); i++) {
            AIMesh mesh = AIMesh.create(Objects.requireNonNull(scene.mMeshes()).get(Objects.requireNonNull(node.mMeshes()).get(i)));
            meshes.add(processMesh(mesh, scene));
        }
        for (int i = 0; i < node.mNumChildren(); i++) {
            processNode(AINode.create(Objects.requireNonNull(node.mChildren()).get(i)), scene);
        }
    }

    @SneakyThrows
    private Mesh processMesh(AIMesh mesh, AIScene scene) {
        ArrayList<Vertex> vertices = new ArrayList<>();
        ArrayList<Integer> indices = new ArrayList<>();

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
            material = materials.get(materialIdx).clone();
        } else {
            material = new Material();
        }

        return new Mesh(StandardCharsets.UTF_8.decode(mesh.mName().data()).toString(), vertices, indices, material);
    }

}
