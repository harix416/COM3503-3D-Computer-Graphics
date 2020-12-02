// package lib;

// public class ModelLightNode {
    
// }
package lib;
import com.jogamp.opengl.*;

import lib.gmaths.Vec3;

public class ModelLightNode extends SGNode {

    protected Model model;
    private final Light light;

    public ModelLightNode(String name, Light l) {
        super(name);
        light = l;
    }

    public void draw(GL3 gl) {
        float[] f = worldTransform.toFloatArrayForGLSL();
        
        // light.setPosition(Vec3 Vec3(f[12], f[13], f[14]));

        // light.setDirection(worldTransform.getRotationVec());
        // light.render(gl, worldTransform);

        model.render(gl, worldTransform);
        for (int i = 0; i < children.size(); i++) {
            children.get(i).draw(gl);
        }
    }



}
