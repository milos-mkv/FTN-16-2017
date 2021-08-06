package gfx;

import exceptions.InvalidDocumentException;
import lombok.Getter;
import managers.TextureManager;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import utils.Disposable;

import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Model extends TransformComponent implements Disposable {

    @Getter
    private List<Mesh> meshes = new ArrayList<>();

    @Getter
    private List<Material> materials = new ArrayList<>();

    @Getter
    private String path;

    public Model(List<Mesh> meshes, List<Material> materials) {
        super();
        this.meshes = meshes;
        this.materials = materials;
    }

    public Model(String resourcePath) throws InvalidDocumentException {
        super();
        path = resourcePath;
        AIScene scene = Assimp.aiImportFile(resourcePath, Assimp.aiProcess_Triangulate | Assimp.aiProcess_FlipUVs
                | Assimp.aiProcess_GenSmoothNormals | Assimp.aiProcess_CalcTangentSpace);

        if (scene == null) {
            throw new InvalidDocumentException("Failed to load model: " + resourcePath);
        }

        PointerBuffer aiMaterials = scene.mMaterials();
        for (var i = 0; i < scene.mNumMaterials(); i++) {
            var aiMaterial = AIMaterial.create(Objects.requireNonNull(aiMaterials).get(i));
            processMaterial(aiMaterial, resourcePath.substring(0, resourcePath.lastIndexOf("/")));
        }
        processNode(Objects.requireNonNull(scene.mRootNode()), scene);
    }

    public void draw(ShaderProgram shaderProgram) {
        shaderProgram.setUniformMat4("model", getTransform());
        meshes.forEach(mesh -> mesh.draw(shaderProgram));
    }

    private Texture getMaterialTexture(AIMaterial material, String texturesDir, int type) {
        var buffer = AIString.calloc();
        Assimp.aiGetMaterialTexture(material, type, 0, buffer, (IntBuffer) null, null, null, null, null, null);
        if (!buffer.dataString().equals("")) {
            return TextureManager.getInstance().getTexture(texturesDir + "/" + buffer.dataString());
        }
        return null;
    }

    private Vector3f getMaterialColor(AIMaterial material, String type) {
        var color = AIColor4D.create();
        int result = Assimp.aiGetMaterialColor(material, type, Assimp.aiTextureType_NONE, 0, color);
        return (result == 0)
                ? new Vector3f(color.r(), color.g(), color.b())
                : new Vector3f(1, 1 ,1);
    }

    private void processMaterial(AIMaterial aiMaterial, String texturesDir) {
        var material = new Material();
        material.setAmbientColor(getMaterialColor(aiMaterial, Assimp.AI_MATKEY_COLOR_AMBIENT));
        material.setDiffuseColor(getMaterialColor(aiMaterial, Assimp.AI_MATKEY_COLOR_DIFFUSE));
        material.setSpecularColor(getMaterialColor(aiMaterial, Assimp.AI_MATKEY_COLOR_SPECULAR));

        material.setShininess(1.0f);
        material.setDiffuseTexture(getMaterialTexture(aiMaterial, texturesDir, Assimp.aiTextureType_DIFFUSE));
        material.setSpecularTexture(getMaterialTexture(aiMaterial, texturesDir, Assimp.aiTextureType_SPECULAR));
        material.setNormalTexture(getMaterialTexture(aiMaterial, texturesDir, Assimp.aiTextureType_HEIGHT));
        var buffer = AIString.calloc();
        Assimp.aiGetMaterialString(aiMaterial, Assimp.AI_MATKEY_NAME, 0, 0, buffer);
        material.setName(buffer.dataString());
        materials.add(material);
    }

    private void processNode(AINode node, AIScene scene) {
        for (var i = 0; i < node.mNumMeshes(); i++) {
            var mesh = AIMesh.create(Objects.requireNonNull(scene.mMeshes()).get(Objects.requireNonNull(node.mMeshes()).get(i)));
            meshes.add(processMesh(mesh));
        }
        for (var i = 0; i < node.mNumChildren(); i++) {
            processNode(AINode.create(Objects.requireNonNull(node.mChildren()).get(i)), scene);
        }
    }

    private Mesh processMesh(AIMesh mesh) {
        var vertices = new ArrayList<Vertex>();
        var indices = new ArrayList<Integer>();

        for (var i = 0; i < mesh.mNumVertices(); i++) {
            var vertex = new Vertex();
            vertex.setPosition(new Vector3f(mesh.mVertices().get(i).x(), mesh.mVertices().get(i).y(), mesh.mVertices().get(i).z()));

            if (mesh.mNormals() != null) {
                vertex.setNormal(new Vector3f(mesh.mNormals().get(i).x(), mesh.mNormals().get(i).y(), mesh.mNormals().get(i).z()));
            }

            if (mesh.mTextureCoords().get(0) != 0) {
                vertex.setTexCoords(new Vector2f(mesh.mTextureCoords(0).get(i).x(), mesh.mTextureCoords(0).get(i).y()));
            } else {
                vertex.setTexCoords(new Vector2f(0, 0));
            }
            vertices.add(vertex);
        }

        for (var i = 0; i < mesh.mNumFaces(); i++) {
            AIFace face = mesh.mFaces().get(i);
            for (var j = 0; j < face.mNumIndices(); j++) {
                indices.add(face.mIndices().get(j));
            }
        }

        Material material = mesh.mMaterialIndex() >= 0 && mesh.mMaterialIndex() < materials.size()
                ? materials.get(mesh.mMaterialIndex()) : new Material();

        return new Mesh(StandardCharsets.UTF_8.decode(mesh.mName().data()).toString(), vertices, indices, material);
    }

    @Override
    public void dispose() {
        meshes.forEach(Mesh::dispose);
    }
}
