import lib.*;
import lib.gmaths.*;
import shapes.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
  
public class M04_GLEventListener implements GLEventListener {
  
  private static final boolean DISPLAY_SHADERS = false;
    
  public M04_GLEventListener(Camera camera) {
    this.camera = camera;
    this.camera.setPosition(new Vec3(4f,12f,18f));
  }
  
  // ***************************************************
  /*
   * METHODS DEFINED BY GLEventListener
   */

  /* Initialisation */
  public void init(GLAutoDrawable drawable) {   
    GL3 gl = drawable.getGL().getGL3();
    System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
    gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f); 
    gl.glClearDepth(1.0f);
    gl.glEnable(GL.GL_DEPTH_TEST);
    gl.glDepthFunc(GL.GL_LESS);
    gl.glFrontFace(GL.GL_CCW);    // default is 'CCW'
    gl.glEnable(GL.GL_CULL_FACE); // default is 'not enabled'
    gl.glCullFace(GL.GL_BACK);   // default is 'back', assuming CCW
    initialise(gl);
    startTime = getSeconds();
  }
  
  /* Called to indicate the drawing surface has been moved and/or resized  */
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    GL3 gl = drawable.getGL().getGL3();
    gl.glViewport(x, y, width, height);
    float aspect = (float)width/(float)height;
    camera.setPerspectiveMatrix(Mat4Transform.perspective(45, aspect));
  }

  /* Draw */
  public void display(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    render(gl);
  }

  /* Clean up memory, if necessary */
  public void dispose(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    light.dispose(gl);
    floor.dispose(gl);
    sphere.dispose(gl);
    // cube.dispose(gl);
    // cube2.dispose(gl);
    backWall.dispose(gl);
    sideWall.dispose(gl);

    desktop.dispose(gl);
    deskLeg0.dispose(gl);
    deskLeg1.dispose(gl);
    deskLeg2.dispose(gl);
    deskLeg3.dispose(gl);
    
    //helicopter
    helicopterBody.dispose(gl);
    helicopterHead.dispose(gl);
    helicopterWing0.dispose(gl);
    helicopterWing1.dispose(gl);

  }
  
  
  // ***************************************************
  /* INTERACTION
  */
  

  public void incXPosition() {
    xPosition += 0.5f;
    if (xPosition>5f) xPosition = 5f;
    updateX();
  }
  
  public void decXPosition() {
    xPosition -= 0.5f;
    if (xPosition<-5f) xPosition = -5f;
    updateX();
  }
  
  private void updateX() {
    translateX.setTransform(Mat4Transform.translate(xPosition,0,0));
    translateX.update(); // IMPORTANT – the scene graph has changed
  }


  // ***************************************************
  /* THE SCENE
   * Now define all the methods to handle the scene.
   * This will be added to in later examples.
   */

  private Camera camera;
  private Model floor, sphere, cube, cube2, 
                backWall, sideWall, sideWallBack, 
                desktop, deskLeg0, deskLeg1, deskLeg2, deskLeg3, 
                helicopterBody, helicopterHead, helicopterWing0, helicopterWing1,
                lampBase, lightCase, lampEye,
                paper, board, pen;
  private Light light, lampLight;
  private SGNode twoBranchRoot;
  
  private float xPosition = 0;
  private TransformNode translateX, rotateUpper, rotateAll, rotateUpper2;
  private float rotateAllAngleStart = 25, rotateAllAngle = rotateAllAngleStart;
  private float rotateUpperAngleStart = -60, rotateUpperAngle = rotateUpperAngleStart;
  private float rotateUpperAngleStart1 = -60, rotateUpper2Angle = rotateUpperAngleStart1;

  private Mesh cubeMesh, sphereMesh, 
                roomMesh, boardMesh,
                lampBaseMesh, lightCaseMesh, lampEyeMesh,
                paperMesh, penMesh;
  private Shader cubeShader, sphereShader, 
                  roomShader, boardShader,
                  lampBaseShader, lightCaseShader, lampEyeShader,
                  paperShader, penShader;
  private Material cubeMaterial, sphereMaterial, 
                    floorMaterial, wallMaterial, boardMaterial,
                    lampBaseMaterial, lightaseMaterial, lampEyeMaterial,
                    paperMaterial, penMaterial;
  private Mat4 desktopModelMatrix, deskLeg0ModelMatrix, deskLeg1ModelMatrix, deskLeg2ModelMatrix, deskLeg3ModelMatrix,
                lampBaseModelMatrix, lightCaseModelMatrix, lampEyeModelMatrix, penModelMatrix;
  

  private void initialise(GL3 gl) {



    createRandomNumbers();

    //texture
    int[] textureId0 = TextureLibrary.loadTexture(gl, "textures/chequerboard.jpg");
    int[] textureId1 = TextureLibrary.loadTexture(gl, "textures/jade.jpg");
    int[] textureId2 = TextureLibrary.loadTexture(gl, "textures/jade_specular.jpg");
    int[] textureId3 = TextureLibrary.loadTexture(gl, "textures/container2.jpg");
    int[] textureId4 = TextureLibrary.loadTexture(gl, "textures/container2_specular.jpg");
    int[] textureId5 = TextureLibrary.loadTexture(gl, "textures/wattBook.jpg");
    int[] textureId6 = TextureLibrary.loadTexture(gl, "textures/wattBook_specular.jpg");

    int[] woodTexture = TextureLibrary.loadTexture(gl, "textures/wood.jpg");
    int[] tilesTexture = TextureLibrary.loadTexture(gl, "textures/wood.jpg");
    int[] viewTexture = TextureLibrary.loadTexture(gl, "textures/wood.jpg");
    int[] MetalTexture = TextureLibrary.loadTexture(gl, "textures/wood.jpg");
    
        
    light = new Light(gl);
    light.setCamera(camera);
    
    roomInit(gl);
    deskInit(gl);
    helicopterInit(gl);
    lampInit(gl);

}



  /**
   * The models init Func
   * 
   * 
   */

  //clear the instance
  private void roomInit(GL3 gl) {
    //texture
    int[] textureId0 = TextureLibrary.loadTexture(gl, "textures/chequerboard.jpg");
    int[] textureId1 = TextureLibrary.loadTexture(gl, "textures/jade.jpg");
    int[] textureId2 = TextureLibrary.loadTexture(gl, "textures/jade_specular.jpg");
    int[] textureId3 = TextureLibrary.loadTexture(gl, "textures/container2.jpg");
    int[] textureId4 = TextureLibrary.loadTexture(gl, "textures/container2_specular.jpg");
    int[] textureId5 = TextureLibrary.loadTexture(gl, "textures/wattBook.jpg");
    int[] textureId6 = TextureLibrary.loadTexture(gl, "textures/wattBook_specular.jpg");


    int[] floorTexture = TextureLibrary.loadTexture(gl, "textures/chequerboard.jpg");
    // int[] backWalltexture = TextureLibrary.loadTexture(gl, "textures/mar0kuu2_specular.jpg");
    int[] backWalltexture = TextureLibrary.loadTexture(gl, "textures/wall.jpg");
    int[] sideWalltexture = TextureLibrary.loadTexture(gl, "textures/bricks.jpg");
    // int[] sideWallBacktexture = TextureLibrary.loadTexture(gl, "textures/ear0xuu2.jpg");
    int[] sideWallBacktexture = TextureLibrary.loadTexture(gl, "textures/view.jpg");
    int[] boardTexture = TextureLibrary.loadTexture(gl, "textures/darkWood.jpg");
    int[] tilesTexture = TextureLibrary.loadTexture(gl, "textures/tiles.jpg");
    int[] metalTexture = TextureLibrary.loadTexture(gl, "textures/metal.jpg");


    roomMesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    roomShader = new Shader(gl, "shaders/vs_tt_05.txt", "shaders/fs_tt_05.txt");
    floorMaterial = new Material(new Vec3(1.0f, 1.0f, 1.0f), new Vec3(1.4f, 1.4f, 1.4f), new Vec3(0.5f, 0.5f, 0.5f), 48.0f);
    wallMaterial = new Material(new Vec3(1.0f, 1.0f, 1.0f), new Vec3(1.2f, 1.2f, 1.2f), new Vec3(0.3f, 0.3f, 0.3f), 32.0f);
    floor = new Model(gl, camera, light, roomShader, floorMaterial, new Mat4(1), roomMesh, tilesTexture);
    backWall = new Model(gl, camera, light, roomShader, wallMaterial, new Mat4(1), roomMesh, backWalltexture);
    sideWall = new Model(gl, camera, light, roomShader, wallMaterial, new Mat4(1), roomMesh, sideWalltexture);
    sideWallBack = new Model(gl, camera, light, roomShader, wallMaterial, new Mat4(1), roomMesh, sideWallBacktexture);

    boardMesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    boardShader = new Shader(gl, "shaders/vs_cube_04.txt", "shaders/fs_cube_04.txt");
    boardMaterial = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    board = new Model(gl, camera, light, boardShader, boardMaterial, new Mat4(1), boardMesh, boardTexture);

    paperMesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    paperShader = new Shader(gl, "shaders/vs_tt_05.txt", "shaders/fs_tt_05.txt");
    paperMaterial = new Material(new Vec3(1.0f, 1.0f, 1.0f), new Vec3(2f, 2f, 2f), new Vec3(0.3f, 0.3f, 0.3f), 32.0f);
    paper = new Model(gl, camera, light, paperShader, paperMaterial, new Mat4(1), paperMesh, textureId5);

    penMesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
    penShader = new Shader(gl, "shaders/vs_cube_04.txt", "shaders/fs_cube_04.txt");
    penMaterial = new Material(new Vec3(1.0f, 1.0f, 1.0f), new Vec3(1f, 1f, 1f), new Vec3(10f, 10f, 10f), 64.0f);
    penModelMatrix = Mat4.multiply(Mat4Transform.scale(0.1f,0.1f,3), Mat4Transform.translate(17,66.7f,-2));
    pen = new Model(gl, camera, light, penShader, penMaterial, penModelMatrix, penMesh, metalTexture, textureId2);
  }
    
  private void deskInit(GL3 gl) {

    float trans = -1.1f;

    float deskLength = 12.0f;
    float deskThickness = 0.4f;
    float deskWidth = 6.0f;
    float deskHeight = 16.0f;

    float legsLength = 6.5f;
    float legsWidth = 0.5f;

    float varM = deskWidth/legsWidth;

    int[] texture1 = TextureLibrary.loadTexture(gl, "textures/wood.jpg");
    int[] texture2 = TextureLibrary.loadTexture(gl, "textures/container2_specular.jpg");

    cubeMesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    cubeShader = new Shader(gl, "shaders/vs_cube_04.txt", "shaders/fs_cube_04.txt");
    cubeMaterial = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);

    // scale: (length，thickness, width)      
    //translate: (left&right， up&down， front&back)
    desktopModelMatrix = Mat4.multiply(Mat4Transform.scale(deskLength, deskThickness, deskWidth), Mat4Transform.translate(0.0f, deskHeight, 0.0f + trans));
    desktop = new Model(gl, camera, light, cubeShader, cubeMaterial, desktopModelMatrix, cubeMesh, texture1, texture2);

    deskLeg0ModelMatrix = Mat4.multiply(Mat4Transform.scale(legsWidth, legsLength, legsWidth), Mat4Transform.translate(-11.0f, 0.5f, 5.0f + varM*trans));
    deskLeg0 = new Model(gl, camera, light, cubeShader, cubeMaterial, deskLeg0ModelMatrix, cubeMesh, texture1, texture2);

    deskLeg1ModelMatrix = Mat4.multiply(Mat4Transform.scale(legsWidth, legsLength, legsWidth), Mat4Transform.translate(-11.0f, 0.5f, -5.0f + varM*trans));
    deskLeg1 = new Model(gl, camera, light, cubeShader, cubeMaterial, deskLeg1ModelMatrix, cubeMesh, texture1, texture2);

    deskLeg2ModelMatrix = Mat4.multiply(Mat4Transform.scale(legsWidth, legsLength, legsWidth), Mat4Transform.translate(11.0f, 0.5f, 5.0f + varM*trans));
    deskLeg2 = new Model(gl, camera, light, cubeShader, cubeMaterial, deskLeg2ModelMatrix, cubeMesh, texture1, texture2);

    deskLeg3ModelMatrix = Mat4.multiply(Mat4Transform.scale(legsWidth, legsLength, legsWidth), Mat4Transform.translate(11.0f, 0.5f, -5.0f + varM*trans));
    deskLeg3 = new Model(gl, camera, light, cubeShader, cubeMaterial, deskLeg3ModelMatrix, cubeMesh, texture1, texture2);

  }



  private void helicopterInit(GL3 gl) {
    // float sphere2Height = 3f;     // *** added variable to make subsequent calculations easier
    int[] textureId3 = TextureLibrary.loadTexture(gl, "textures/container2_specular.jpg");
    int[] textureId4 = TextureLibrary.loadTexture(gl, "textures/container2_specular.jpg");
    
    sphereMesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
    sphereShader = new Shader(gl, "vs_sphere_04.txt", "fs_sphere_04.txt");
    sphereMaterial = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);

    helicopterBody = new Model(gl, camera, light, sphereShader, sphereMaterial, new Mat4(1), sphereMesh, textureId3, textureId4);
    helicopterHead = new Model(gl, camera, light, sphereShader, sphereMaterial, new Mat4(1), sphereMesh, textureId3, textureId4);
    helicopterWing0 = new Model(gl, camera, light, sphereShader, sphereMaterial, new Mat4(1), sphereMesh, textureId3, textureId4);
    helicopterWing1 = new Model(gl, camera, light, sphereShader, sphereMaterial, new Mat4(1), sphereMesh, textureId3, textureId4);
  }


  private void lampInit(GL3 gl) {


    int[] textureId1 = TextureLibrary.loadTexture(gl, "textures/jade.jpg");
    int[] textureId2 = TextureLibrary.loadTexture(gl, "textures/jade_specular.jpg");

    //translate 
    float xx = -4f, yy = 6.8f, zz = -7f;
    String xxs = "-4.0", yys = "6.8", zzs = "-7.0";
    //scale lampStructure  width, height, thuickness
    float sx = 0.3f , sy = 2.0f, sz = 0.3f;
    String sxs = "0.3", sys = "2.0", szs = "0.3";
    //scale lightCase
    float cx = 0.5f , cy = 1.5f, cz = 0.5f;
    String cxs = "0.5", cys = "1.5", czs = "0.5";

    lampBaseMesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    lampBaseShader = new Shader(gl, "shaders/vs_cube_04.txt", "shaders/fs_cube_04.txt");
    lampBaseMaterial = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    lampBaseModelMatrix = Mat4.multiply(Mat4Transform.scale(1.5f, 0.3f, 1.0f), Mat4Transform.translate(-2.5f, 22.2f, -7f));  //scale trans
    lampBase = new Model(gl, camera, light, lampBaseShader, lampBaseMaterial, lampBaseModelMatrix, lampBaseMesh, textureId1, textureId2);

    lightCaseMesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    lightCaseShader = new Shader(gl, "shaders/vs_cube_04.txt", "shaders/fs_cube_04.txt");
    lightaseMaterial = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    lightCaseModelMatrix = Mat4.multiply(Mat4Transform.scale(1.0f, 1.0f, 1.0f), Mat4Transform.translate(0.0f, 0.5f, 0.0f));  //scale trans
    lightCase = new Model(gl, camera, light, lightCaseShader, lightaseMaterial, lightCaseModelMatrix, lightCaseMesh, textureId1, textureId2);

    lampEyeMesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
    lampEyeShader = new Shader(gl, "shaders/vs_cube_04.txt", "shaders/fs_cube_04.txt");
    lampEyeMaterial = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    lampEyeModelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
    lampEye = new Model(gl, camera, light, lampEyeShader, lampEyeMaterial, lampEyeModelMatrix, lampEyeMesh, textureId1, textureId2);

    Mesh mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
    Shader shader = new Shader(gl, "shaders/vs_cube_04.txt", "shaders/fs_cube_04.txt");
    Material material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    Mat4 modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
    sphere = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId1, textureId2);


    twoBranchRoot = new NameNode("two-branch structure");
    translateX = new TransformNode("translate("+xxs+","+yys+","+zzs+")", Mat4Transform.translate(xx,yy,zz));
    rotateAll = new TransformNode("rotateAroundZ("+rotateAllAngle+")", Mat4Transform.rotateAroundZ(rotateAllAngle));
    
    NameNode lowerBranch = new NameNode("lower branch");
    Mat4 m = Mat4Transform.scale(sx,sy,sz);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));

    TransformNode makeLowerBranch = new TransformNode("scale("+sxs+","+sys+","+szs+"); translate(0,0.5f,0)", m);
    ModelNode cube0Node = new ModelNode("Sphere(0)", sphere);

    TransformNode translateToTop = new TransformNode("translate(0,"+sys+",0)",Mat4Transform.translate(0,sy,0));
    rotateUpper = new TransformNode("rotateAroundZ("+rotateUpperAngle+")",Mat4Transform.rotateAroundZ(rotateUpperAngle));
    NameNode upperBranch = new NameNode("upper branch");

    m = Mat4Transform.scale(sx,sy,sz);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));

    TransformNode makeUpperBranch = new TransformNode("scale("+sxs+","+sys+","+szs+");translate(0,0.5f,0)", m);
    ModelNode cube1Node = new ModelNode("Sphere(1)", sphere);
    lampLight = new Light(gl);
    lampLight.setCamera(camera);

    TransformNode translateToTop2 = new TransformNode("translate(0,"+"2.0"+",0)",Mat4Transform.translate(0,sy,0));
    rotateUpper2 = new TransformNode("rotateAroundZ("+rotateUpper2Angle+")",Mat4Transform.rotateAroundZ(rotateUpper2Angle));
    NameNode upper2Branch = new NameNode("upper 2 branch");
    m = Mat4Transform.scale(cx,cy,cz);
    m = Mat4.multiply(m, Mat4Transform.translate(0,-0.1f,0));
    TransformNode makeUpper2Branch = new TransformNode("scale("+cxs+","+cys+","+czs+");translate(0,0,0)", m);
    ModelNode cube2Node = new ModelNode("lightCase", lightCase);

    // ModelNode lampEye = new ModelNode("lampEye");
    // ModelNode cube2LightNode = new ModelNode("lampLight", lampLight);
    // NameNode lampLightName = new NameNode("lampLight");
    // m = Mat4Transform.scale(0.3f,0.3f,0.3f);
    // m = Mat4.multiply(Mat4Transform.translate(0, 0.15f, 0), m);
    // TransformNode lampLightTransform = new TransformNode("lampLightTransform", m);
    // ModelNode lampLightNode = new ModelNode("lampLightNode", lampLight);

    twoBranchRoot.addChild(translateX);
      translateX.addChild(rotateAll);
        rotateAll.addChild(lowerBranch);
          lowerBranch.addChild(makeLowerBranch);
            makeLowerBranch.addChild(cube0Node);
          lowerBranch.addChild(translateToTop);
            translateToTop.addChild(rotateUpper);
              rotateUpper.addChild(upperBranch);
                upperBranch.addChild(makeUpperBranch);
                  makeUpperBranch.addChild(cube1Node);
                upperBranch.addChild(translateToTop2);
                  translateToTop2.addChild(rotateUpper2);
                    rotateUpper2.addChild(upper2Branch);
                      upper2Branch.addChild(makeUpper2Branch);
                        makeUpper2Branch.addChild(cube2Node);
                        // makeUpper2Branch.addChild(lampEye);
    twoBranchRoot.update();  // IMPORTANT – must be done every time any part of the scene graph changes
  }




  private void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    // light.setPosition(getLightPosition());  // changing light position each frame
    light.setPosition(new Vec3(0,30f,0));
    light.render(gl);

    if (lightOn) {
      light.setPosition(new Vec3(0,12f,-10));
      light.render(gl);

      light.setPosition(new Vec3(-20f,10f,0));
      light.render(gl);
    }

    roomRender(gl);
    deskRender(gl);
    helicopterRender(gl);

    updateBranches();
    twoBranchRoot.draw(gl);
    lampBase.render(gl);

    


    // if (animation) updateLeftArm();
    // robotRoot.draw(gl);
  }

  /**
   * Render functions
   * @param gl
   */

  private void roomRender(GL3 gl) {
    //floor
    floor.setModelMatrix(getMforFloor());
    floor.render(gl); 
    //backwall
    backWall.setModelMatrix(getMforBackWall());   
    backWall.render(gl);
    Mat4 boardMatrix = getMforBackWall();
    boardMatrix = Mat4.multiply(Mat4Transform.scale(0.5f,0.3f,0.3f), boardMatrix);
    boardMatrix = Mat4.multiply(Mat4Transform.translate(0.0f,10f,-6.9f), boardMatrix);
    board.setModelMatrix(boardMatrix);
    board.render(gl);

    Mat4 paperM = Mat4.multiply(Mat4Transform.scale(2,2,4), Mat4Transform.translate(0.8f, -4.87f,-3.5f));
    paper.setModelMatrix(Mat4.multiply(Mat4.multiply(Mat4Transform.rotateAroundX(90) ,Mat4Transform.rotateAroundY(20)), paperM));
    paper.render(gl);

    paperM = Mat4.multiply(Mat4Transform.scale(2,2,4), Mat4Transform.translate(-0.3f, -4.87f,-3.2f));
    paper.setModelMatrix(Mat4.multiply(Mat4.multiply(Mat4Transform.rotateAroundX(90) ,Mat4Transform.rotateAroundY(-15)), paperM));
    paper.render(gl);
    //sidewall

    Mat4 sideWallM = getMforSideWall();
    sideWallM = Mat4.multiply(Mat4Transform.scale(1/3f,1/3f,1/3f), sideWallM);
    sideWallM = Mat4.multiply(Mat4Transform.translate(-20/3f,20/3f,20/3f), sideWallM);
    sideWall.setModelMatrix(sideWallM);
    sideWall.render(gl);
    sideWallM = Mat4.multiply(Mat4Transform.translate(0,0,-40/3f), sideWallM);
    sideWall.setModelMatrix(sideWallM);
    sideWall.render(gl);

    for (int i = 0; i < 3; i++) {

      sideWallM = getMforSideWall();
      sideWallM = Mat4.multiply(Mat4Transform.scale(1/3f,1/3f,1/3f), sideWallM);
      sideWallM = Mat4.multiply(Mat4Transform.translate(-20/3f,0,20/3f-20/3f*i), sideWallM);
      sideWall.setModelMatrix(sideWallM);
      sideWall.render(gl);
      sideWallM = Mat4.multiply(Mat4Transform.translate(0,40/3f,0), sideWallM);
      sideWall.setModelMatrix(sideWallM);
      sideWall.render(gl);

    }


    Mat4 sideWallBackM = getMforSideWall();
    sideWallBackM = Mat4.multiply(Mat4Transform.translate(-5f,0,0), sideWallBackM);
    sideWallBack.setModelMatrix(sideWallBackM);
    sideWallBack.render(gl);
  }

  private void deskRender(GL3 gl) {
    desktop.render(gl);
    deskLeg0.render(gl);
    deskLeg1.render(gl);
    deskLeg2.render(gl);
    deskLeg3.render(gl);
    
    paper.setModelMatrix(Mat4.multiply(Mat4Transform.scale(2,2,4), Mat4Transform.translate(0.0f, 3.33f,-1.5f)));
    paper.render(gl);
    pen.render(gl);
    
  }

  private void helicopterRender(GL3 gl) {

    float helicopterBodyHeight = 1.0f;
    float helicopterHeadHeight = 0.4f;

    float helicopterWingsLength = 1.5f;
    float helicopterWingsThick = 0.1f;
    float helicopterWingsWidth = 0.35f;

    float helicopterBodyLR = 4.0f;  // left&right
    float helicopterBodyUD;
    // float helicopterBodyUD = 7.1f
    // float helicopterBodyUD = 4f+3.1f*(float)((2+Math.cos(-Math.toRadians(elapsedTime*40))));
    if (animation) {
      double elapsedTime = getSeconds()-startTime;
      helicopterBodyUD = 4f+3.1f*(float)((2+Math.cos(Math.toRadians(elapsedTime*40))));
    } else {
      helicopterBodyUD = 7.1f;
    }
    float helicopterBodyFB = -7.7f; //front&back
    float varM = helicopterBodyHeight/helicopterHeadHeight;

    Mat4 helicopterBodyModelMatrix = Mat4.multiply(Mat4Transform.scale(helicopterBodyHeight,helicopterBodyHeight,helicopterBodyHeight), 
                                                                       Mat4Transform.translate(helicopterBodyLR,helicopterBodyUD,helicopterBodyFB));                                                              
    // helicopterBodyModelMatrix = Mat4.multiply(Mat4Transform.rotateAroundY(60),helicopterBodyModelMatrix);
    helicopterBody.setModelMatrix(helicopterBodyModelMatrix);
    helicopterBody.render(gl);


    Mat4 helicopterHeadModelMatrix = Mat4.multiply(Mat4Transform.scale(helicopterHeadHeight,helicopterHeadHeight,helicopterHeadHeight), 
                                                        Mat4Transform.translate(helicopterBodyLR*varM,helicopterBodyUD*varM + 1.5f,helicopterBodyFB*varM));                                                
    helicopterHead.setModelMatrix(helicopterHeadModelMatrix);                                          
    helicopterHead.render(gl);

    Mat4 helicopterWingModelMatrix0 = Mat4.multiply(Mat4Transform.scale(helicopterWingsLength,helicopterWingsThick,helicopterWingsWidth), 
                                                                            Mat4Transform.translate(helicopterBodyLR*helicopterBodyHeight/helicopterWingsLength + 0.5f,
                                                                                                    helicopterBodyUD*helicopterBodyHeight/helicopterWingsThick + 5.5f,
                                                                                                    helicopterBodyFB*helicopterBodyHeight/helicopterWingsWidth));
    // helicopterWingModelMatrix0 = Mat4.multiply(Mat4Transform.rotateAroundY(180),helicopterWingModelMatrix0);
    helicopterWing0.setModelMatrix(helicopterWingModelMatrix0);
    helicopterWing0.render(gl);


    Mat4 helicopterWingModelMatrix1 = Mat4.multiply(Mat4Transform.scale(helicopterWingsLength,helicopterWingsThick,helicopterWingsWidth), 
                                                                        Mat4Transform.translate(helicopterBodyLR*helicopterBodyHeight/helicopterWingsLength - 0.5f,
                                                                                                helicopterBodyUD*helicopterBodyHeight/helicopterWingsThick + 5.5f,
                                                                                                helicopterBodyFB*helicopterBodyHeight/helicopterWingsWidth));
    helicopterWing1.setModelMatrix(helicopterWingModelMatrix1);
    helicopterWing1.render(gl);
  }



  private void updateBranches() {
    // float elapsedTime = (float)(getSeconds()-startTime);
    double elapsedTime = getSeconds()-startTime;
    // rotateAllAngle = rotateAllAngleStart*(float)Math.sin(elapsedTime);
    // rotateUpperAngle = rotateUpperAngleStart*(float)Math.sin(elapsedTime*0.7f);
    // rotateUpper2Angle = rotateUpperAngleStart*(float)Math.sin(elapsedTime*0.9f);
    if (elapsedTime < 1) {
      rotateAllAngle = rotateAllAngleStart*(float)Math.sin(elapsedTime);
      rotateUpperAngle = rotateUpperAngleStart*(float)Math.sin(elapsedTime*0.7f);
      rotateUpper2Angle = rotateUpperAngleStart*(float)Math.sin(elapsedTime*0.9f);
    }

    // double nowElapsedTime = elapsedTime;
    if (randomPosition) {
      double nowElapsedTime = Double.POSITIVE_INFINITY;
      if(nowElapsedTime > elapsedTime*100){nowElapsedTime = elapsedTime;}
      // double nowElapsedTime = elapsedTime;
      if (elapsedTime - nowElapsedTime < 2) {
          rotateAllAngle = rotateAllAngleStart*(float)Math.sin(elapsedTime);
          rotateUpperAngle = rotateUpperAngleStart*(float)Math.sin(elapsedTime*0.7f);
          rotateUpper2Angle = rotateUpperAngleStart*(float)Math.sin(elapsedTime*0.9f);
      }
      System.out.println("------------------------------------------");
      System.out.println(elapsedTime);
      System.out.println(nowElapsedTime);
    }


    rotateAll.setTransform(Mat4Transform.rotateAroundX(rotateAllAngle));

    if ((int)Math.random()%2 == 1) {
      rotateUpper.setTransform(Mat4Transform.rotateAroundX(rotateUpperAngle));
    }else{
      rotateUpper.setTransform(Mat4Transform.rotateAroundZ(rotateUpperAngle));
    }

    
    if ((int)Math.random()%2 == 1) {
      rotateUpper2.setTransform(Mat4Transform.rotateAroundX(rotateUpper2Angle));
    }else{
      rotateUpper2.setTransform(Mat4Transform.rotateAroundZ(rotateUpper2Angle));
    }



    twoBranchRoot.update(); // IMPORTANT – the scene graph has changed
  }






  //get M for func!!





  private float roomSize = 20f;
  // As the transforms do not change over time for this object, they could be stored once rather than continually being calculated
  private Mat4 getMforFloor() {
    Mat4 modelMatrix = new Mat4(1);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(roomSize,1f,roomSize), modelMatrix);
    return modelMatrix;
  }

  // As the transforms do not change over time for this object, they could be stored once rather than continually being calculated
  private Mat4 getMforBackWall() {
    Mat4 modelMatrix = new Mat4(1);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(roomSize,1f,roomSize), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(90), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(0,roomSize*0.5f,-roomSize*0.5f), modelMatrix);
    return modelMatrix;
  }

  // As the transforms do not change over time for this object, they could be stored once rather than continually being calculated
  private Mat4 getMforSideWall() {
    Mat4 modelMatrix = new Mat4(1);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(roomSize,1f,roomSize), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundY(90), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundZ(-90), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(-roomSize*0.5f,roomSize*0.5f,0), modelMatrix);
    return modelMatrix;
  }

  
  // ***************************************************
  /* TIME
   */ 
  
  private double startTime;
  
  private double getSeconds() {
    return System.currentTimeMillis()/1000.0;
  }

  // ***************************************************
  /* An array of random numbers
   */ 
  
  private int NUM_RANDOMS = 1000;
  private float[] randoms;
  private float floatNum = 0f;
  
  private void createRandomNumbers() {
    randoms = new float[NUM_RANDOMS];
    for (int i=0; i<NUM_RANDOMS; ++i) {
      randoms[i] = (float)Math.random();
    }
  }
  
  private boolean animation = false;
  private boolean lightOn = false;
  private boolean randomPosition = false;
  private double savedTime = 0;
   
  public void startAnimation() {
    animation = true;
    startTime = getSeconds()-savedTime;
  }
   
  public void stopAnimation() {
    animation = false;
    double elapsedTime = getSeconds()-startTime;
    savedTime = elapsedTime;
  }

  public void turnOnLight() {
    lightOn = false;
  }

  public void turnOffLight() {
    lightOn = true;
  }

  public void randomPos(){
    randomPosition = true;
    // float elapsedTime = (float)(getSeconds()-startTime);
  }
}