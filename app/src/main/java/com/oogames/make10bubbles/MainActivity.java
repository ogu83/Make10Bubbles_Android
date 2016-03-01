package com.oogames.make10bubbles;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.CalendarContract;
import android.provider.Settings;
import android.text.style.TtsSpan;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.MassData;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.FadeInModifier;
import org.andengine.entity.modifier.FadeOutModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.util.FPSLogger;
import org.andengine.entity.util.ScreenCapture;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.Texture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.TextUtils;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;
import org.andengine.util.math.MathUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;

import javax.net.ssl.HttpsURLConnection;

import static java.util.Collections.synchronizedList;

/**
 * Created by oguzkoroglu on 27/02/16.
 */
public class MainActivity extends SimpleBaseGameActivity implements IAccelerationListener, IOnSceneTouchListener {
    public static MainActivity Instance;

    protected static int CAMERA_WIDTH = 480;
    protected static int CAMERA_HEIGHT = 720;

    private int xMargin = 10;
    private int yMargin = 40;

    private float bubbleRadiusMinDivider = 4;
    private float bubbleRadiusDivider = 6;
    private float levelIntervalInSeconds = 3;

    private int infoSlideMin = 4;
    private int infoSlideMax = 15;
    private int infoSlideCurrent = 4;

    public boolean gamePaused=false;
    public boolean gameOver=false;
    public boolean onMenu=true;
    public boolean onExplode=false;
    public boolean isSoundOn = true;

    public int score;
    public int nextLevelScore = 1;
    public boolean onScoreAction = false;
    public boolean hintsEnabled = true;
    public int hintCountDown = 5;

    private Rectangle ground;
    private Rectangle left;
    private Rectangle right;

    private MenuButton playButton;
    private MenuButton infoButton;
    private MenuButton highScoreButton;
    private MenuButton reviewButton;
    private MenuButton exitButton;

    private MenuButton gotoMenuButton;
    private MenuButton soundButton;
    private MenuButton playPauseButton;

    private TextureRegion backgroundTextureRegion;

    public Text scoreLabel;

    public String UserName;

    private List<NumberBubble> Bubbles;

    protected PhysicsWorld mPhysicsWorld;

    private Scene mScene;

    private Random random = new Random();

    private TimerHandler timer;

    private String android_id = "";

    public MainActivity()
    {
        super();
        Instance = this;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        //Debug.d(String.format("Main Activity with AndroidID: %s", android_id));
    }

    @Override
    public EngineOptions onCreateEngineOptions() {
        //Toast.makeText(this, "Make 10 Bubbles", Toast.LENGTH_LONG).show();
        final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
    }

    @Override
    public void onCreateResources() {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inMutable = true;

        //Background Texture
        Bitmap bmpBackground =  BitmapFactory.decodeResource(getResources(),R.drawable.background);
        BitmapTextureAtlasSource srcBackground = new BitmapTextureAtlasSource(bmpBackground);
        BitmapTextureAtlas backTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), bmpBackground.getWidth(), bmpBackground.getHeight(), TextureOptions.NEAREST);
        backgroundTextureRegion = TextureRegionFactory.createFromSource(backTextureAtlas, srcBackground,0,0);
        backTextureAtlas.load();
        //Play Button Texture
        Bitmap bmpPlay =  BitmapFactory.decodeResource(getResources(),R.drawable.playbutton);
        BitmapTextureAtlasSource srcPlay = new BitmapTextureAtlasSource(bmpPlay);
        BitmapTextureAtlas playTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), bmpPlay.getWidth(), bmpPlay.getHeight(), TextureOptions.NEAREST);
        MenuButton.PlayButtonTextureRegion = TextureRegionFactory.createFromSource(playTextureAtlas, srcPlay,0,0);
        playTextureAtlas.load();
        //Info Button Texture
        Bitmap bmpInfo =  BitmapFactory.decodeResource(getResources(),R.drawable.questionbutton);
        BitmapTextureAtlasSource srcInfo = new BitmapTextureAtlasSource(bmpInfo);
        BitmapTextureAtlas infoTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), bmpInfo.getWidth(), bmpInfo.getHeight(), TextureOptions.NEAREST);
        MenuButton.InfoButtonTextureRegion = TextureRegionFactory.createFromSource(infoTextureAtlas, srcInfo,0,0);
        infoTextureAtlas.load();
        //HighScore Button Texture
        Bitmap bmpHS =  BitmapFactory.decodeResource(getResources(),R.drawable.highscorebutton);
        BitmapTextureAtlasSource srcHS = new BitmapTextureAtlasSource(bmpHS);
        BitmapTextureAtlas hsTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), bmpHS.getWidth(), bmpHS.getHeight(), TextureOptions.NEAREST);
        MenuButton.HighScoreButtonTextureRegion = TextureRegionFactory.createFromSource(hsTextureAtlas, srcHS,0,0);
        hsTextureAtlas.load();
        //Review Button Texture
        Bitmap bmpReview =  BitmapFactory.decodeResource(getResources(),R.drawable.reviewbutton);
        BitmapTextureAtlasSource srcReview = new BitmapTextureAtlasSource(bmpReview);
        BitmapTextureAtlas reviewTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), bmpReview.getWidth(), bmpReview.getHeight(), TextureOptions.NEAREST);
        MenuButton.ReviewButtonTextureRegion = TextureRegionFactory.createFromSource(reviewTextureAtlas, srcReview,0,0);
        reviewTextureAtlas.load();
        //Exit Button Texture
        Bitmap bmpExit =  BitmapFactory.decodeResource(getResources(),R.drawable.closebutton);
        BitmapTextureAtlasSource srcExit = new BitmapTextureAtlasSource(bmpExit);
        BitmapTextureAtlas exitTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), bmpExit.getWidth(), bmpExit.getHeight(), TextureOptions.NEAREST);
        MenuButton.ExitButtonTextureRegion = TextureRegionFactory.createFromSource(exitTextureAtlas, srcExit,0,0);
        exitTextureAtlas.load();

        //Number Textures
        NumberBubble.nTTextureRegions = new ArrayList<>();
        for (int i = 1; i < 10 ; i++) {
            Bitmap b0 = BitmapFactory.decodeResource(getResources(), R.drawable.n1, opt);
            Bitmap b1 = BitmapFactory.decodeResource(getResources(), R.drawable.n1s, opt);
            switch (i)
            {
                default:
                    break;
                case 2:
                    b0 = BitmapFactory.decodeResource(getResources(), R.drawable.n2, opt);
                    b1 = BitmapFactory.decodeResource(getResources(), R.drawable.n2s, opt);
                    break;
                case 3:
                    b0 = BitmapFactory.decodeResource(getResources(), R.drawable.n3, opt);
                    b1 = BitmapFactory.decodeResource(getResources(), R.drawable.n3s, opt);
                    break;
                case 4:
                    b0 = BitmapFactory.decodeResource(getResources(), R.drawable.n4, opt);
                    b1 = BitmapFactory.decodeResource(getResources(), R.drawable.n4s, opt);
                    break;
                case 5:
                    b0 = BitmapFactory.decodeResource(getResources(), R.drawable.n5, opt);
                    b1 = BitmapFactory.decodeResource(getResources(), R.drawable.n5s, opt);
                    break;
                case 6:
                    b0 = BitmapFactory.decodeResource(getResources(), R.drawable.n6, opt);
                    b1 = BitmapFactory.decodeResource(getResources(), R.drawable.n6s, opt);
                    break;
                case 7:
                    b0 = BitmapFactory.decodeResource(getResources(), R.drawable.n7, opt);
                    b1 = BitmapFactory.decodeResource(getResources(), R.drawable.n7s, opt);
                    break;
                case 8:
                    b0 = BitmapFactory.decodeResource(getResources(), R.drawable.n8, opt);
                    b1 = BitmapFactory.decodeResource(getResources(), R.drawable.n8s, opt);
                    break;
                case 9:
                    b0 = BitmapFactory.decodeResource(getResources(), R.drawable.n9, opt);
                    b1 = BitmapFactory.decodeResource(getResources(), R.drawable.n9s, opt);
                    break;
            }
            Bitmap b2 = BitmapHelper.CombineImages(b0, b1);
            BitmapTextureAtlasSource s2 = new BitmapTextureAtlasSource(b2);
            BitmapTextureAtlas mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), b2.getWidth(), b2.getHeight(), TextureOptions.NEAREST);
            ITiledTextureRegion ttr = TextureRegionFactory.createTiledFromSource(mBitmapTextureAtlas, s2,0,0,2,1);
            mBitmapTextureAtlas.load();
            NumberBubble.nTTextureRegions.add(ttr);
        }
    }

    @Override
    public Scene onCreateScene() {
        xMargin = CAMERA_WIDTH / 18;
        yMargin = CAMERA_HEIGHT / 16;

        this.mEngine.registerUpdateHandler(new FPSLogger());

        this.mScene = new Scene();
        this.mScene.setBackground(new Background(0.9f, 0.9f, 1.0f));
        this.mScene.setOnSceneTouchListener(this);

        this.mPhysicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH), false);

        this.mScene.registerUpdateHandler(this.mPhysicsWorld);

        createBackground();
        createBackgroundMusic();
        createMenu();
        Bubbles = synchronizedList(new ArrayList<NumberBubble>());

        timer = new TimerHandler(levelIntervalInSeconds, new ITimerCallback()
        {
            public void onTimePassed(final TimerHandler pTimerHandler)
            {
                if (!onMenu && !gameOver && !gamePaused)
                {
                    checkGameOver();
                    addNumber();
                    mScene.sortChildren();
                    removeOutOfScreenBubbles();
                    checkHint();
                }
                else if (onMenu)
                {
                    //TODO: if (_infoScreen == nil)
                    addNumber();
                    mScene.sortChildren();
                    removeOutOfScreenBubbles();
                }

                timer.setTimerSeconds(onMenu ? 0.25f : levelIntervalInSeconds);
                timer.reset();
            }
        });
        this.mScene.registerUpdateHandler(timer);

        return this.mScene;
    }

    private void createBucket() {
        final VertexBufferObjectManager vertexBufferObjectManager = this.getVertexBufferObjectManager();
        ground = new Rectangle(xMargin, CAMERA_HEIGHT - yMargin, CAMERA_WIDTH-2*xMargin, 5, vertexBufferObjectManager);
        //final Rectangle roof = new Rectangle(0, 0, CAMERA_WIDTH, 2, vertexBufferObjectManager);
        left = new Rectangle(xMargin, yMargin, 5, CAMERA_HEIGHT-2*yMargin, vertexBufferObjectManager);
        right = new Rectangle(CAMERA_WIDTH - xMargin-5, yMargin, 5, CAMERA_HEIGHT-2*yMargin, vertexBufferObjectManager);

        ground.setColor(65f / 255f, 113f / 255f, 156f / 255f);
        left.setColor(ground.getColor());
        right.setColor(ground.getColor());

        final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(2,0.15f,0.15f);
        PhysicsFactory.createBoxBody(this.mPhysicsWorld, ground, BodyDef.BodyType.StaticBody, wallFixtureDef);
        //PhysicsFactory.createBoxBody(this.mPhysicsWorld, roof, BodyDef.BodyType.StaticBody, wallFixtureDef);
        PhysicsFactory.createBoxBody(this.mPhysicsWorld, left, BodyDef.BodyType.StaticBody, wallFixtureDef);
        PhysicsFactory.createBoxBody(this.mPhysicsWorld, right, BodyDef.BodyType.StaticBody, wallFixtureDef);

        this.mScene.attachChild(ground);
        //this.mScene.attachChild(roof);
        this.mScene.attachChild(left);
        this.mScene.attachChild(right);
    }

    @Override
    public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
        if (this.mPhysicsWorld != null) {
            if (pSceneTouchEvent.isActionDown()) {
                //this.addNumber(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
                return true;
            }
        }
        return false;
    }

    @Override
    public void onAccelerationAccuracyChanged(final AccelerationData pAccelerationData) {

    }

    @Override
    public void onAccelerationChanged(final AccelerationData pAccelerationData) {
        final Vector2 gravity = Vector2Pool.obtain(pAccelerationData.getX(), pAccelerationData.getY());
        this.mPhysicsWorld.setGravity(gravity);
        Vector2Pool.recycle(gravity);
    }

    @Override
    public void onResumeGame() {
        super.onResumeGame();
        //this.enableAccelerationSensor(this);
    }

    @Override
    public void onPauseGame() {
        super.onPauseGame();
        //this.disableAccelerationSensor();
    }

    public void DrawScore() {
        runOnUpdateThread(new Runnable() {
            @Override
            public void run() {

                if (scoreLabel == null) {
                    float frameW = CAMERA_WIDTH;
                    float frameH = CAMERA_HEIGHT;
                    //final Font font = FontFactory.create(getFontManager(), getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32);
                    FontFactory.setAssetBasePath("font/");
                    final ITexture fontTexture = new BitmapTextureAtlas(getTextureManager(), 256, 256, TextureOptions.BILINEAR);
                    final Font font = FontFactory.createFromAsset(getFontManager(), fontTexture, getAssets(), "ComicSansMSRegular.ttf", frameH / 32, true, android.graphics.Color.GREEN);
                    font.load();
                    scoreLabel = new Text(2 * xMargin, yMargin / 2 + frameH / 32, font, "Score", 26, getVertexBufferObjectManager());
                    //scoreLabel.setColor(Color.GREEN);
                    scoreLabel.setHorizontalAlign(HorizontalAlign.CENTER);
                    scoreLabel.setWidth(frameW - 4 * xMargin);
                    scoreLabel.setZIndex(9);
                    mScene.attachChild(scoreLabel);
                }

                //scoreLabel.setText(String.format("Score: %d", score));
                if (!onScoreAction) {
                    onScoreAction = true;
                    ScaleModifier m1 = new ScaleModifier(0.25f, 1f, 110f / 100f) {
                        @Override
                        protected void onModifierFinished(IEntity pItem) {
                            super.onModifierFinished(pItem);
                            scoreLabel.setText(String.format("Score: %d", score));
                            ScaleModifier m2 = new ScaleModifier(0.25f, 1f, 100f / 110f) {
                                @Override
                                protected void onModifierFinished(IEntity pItem) {
                                    super.onModifierFinished(pItem);
                                    onScoreAction = false;
                                }
                            };
                            scoreLabel.registerEntityModifier(m2);
                        }
                    };
                    scoreLabel.registerEntityModifier(m1);
                }
            }
        });
    }

    private void levelUp() {
        Debug.d("Level Up");
        nextLevelScore *= 2;
        levelIntervalInSeconds *= 0.8;
        bubbleRadiusDivider -= 0.1;
        bubbleRadiusDivider = Math.max(0,bubbleRadiusDivider);
    }

    public void CalculateScore(NumberBubble sender) {
        Debug.d(String.format("Score: %d", score));
        score += 0.1 * ((100.0 / 4) * 10 / levelIntervalInSeconds);
        if (nextLevelScore < score)
            levelUp();
    }

    private float averageBubble() {
        float average = 0;
        float sum = 0;
        for (NumberBubble b:  Bubbles) {
            sum += b.No;
        }
        average = sum/ Bubbles.size();
        return average;
    }

    private void addNumber() {
        final NumberBubble bubble;
        final FixtureDef objectFixtureDef = PhysicsFactory.createFixtureDef(2, 0.15f, 0.15f);
        final Body body;

        float frameW = CAMERA_WIDTH;
        //float frameH = CAMERA_HEIGHT;
        float nW = frameW / ((float)random.nextInt(10 * (int)bubbleRadiusDivider)/10 +bubbleRadiusMinDivider);

        int no = 5;
        if (averageBubble() > 5)
            no = random.nextInt(5)+1;
        else
            no = random.nextInt(5)+5;

        float pX = xMargin+random.nextInt((int)(frameW-2*xMargin-nW));
        float pY = yMargin;

        bubble = new NumberBubble(pX,pY, NumberBubble.nTTextureRegions.get(no-1),this.getVertexBufferObjectManager(),no,nW);

        body = PhysicsFactory.createCircleBody(this.mPhysicsWorld, bubble, BodyDef.BodyType.DynamicBody, objectFixtureDef);
        body.setFixedRotation(true);
        bubble.Body = body;

        Bubbles.add(bubble);

        this.mScene.attachChild(bubble);
        this.mScene.registerTouchArea(bubble);
        this.mScene.setTouchAreaBindingOnActionDownEnabled(true);

        this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(bubble, body, true, true));
    }

    public void RemoveBubble(final NumberBubble bubble) {
        if (bubble == null) return;
        if (!Bubbles.contains(bubble)) return;
        Bubbles.remove(bubble);
        runOnUpdateThread(new Runnable() {
            @Override
            public void run() {
                bubble.setIgnoreUpdate(true);
                mScene.detachChild(bubble);
                mPhysicsWorld.destroyBody(bubble.Body);
                Debug.d(String.format("Bubble removed no: %d", bubble.No));
                bubble.dispose();
            }
        });
    }

    public void ExplodeBubbles() {
        onExplode = true;
        int sum = 0;
        boolean explode = false;
        boolean deselect = false;
        for (NumberBubble b:  Bubbles) {
            if (b.IsSelected) {
                sum += b.No;
                explode = (sum == 10);
                deselect = sum > 10;
                if (deselect)
                    break;
            }
        }

        if (deselect) {
            for (NumberBubble b:  Bubbles) {
                if ( Bubbles.get(0) == b) b.playWarnSound();
                b.SetSelected(false);
            }
            onExplode = false;
        }
        else if (explode) {
            for (NumberBubble b:  Bubbles) {
                if (b.IsSelected && !b.IsRemoved) {
                    b.IsRemoved = true;
                    b.playWhoopSound();
                    b.ShrinkAction();
                }
            }
        }
        else
            onExplode=false;
    }

    public void checkGameOver() {
        float frameH = CAMERA_HEIGHT;
        for (NumberBubble b: Bubbles) {
            if (b.getY()<yMargin) {
                gameOver();
                return;
            }
            else if (b.getY()<frameH*0.25 + yMargin) {
                b.playWarnSound();
            }
        }
    }

    public void gameOver() {
        gameOver = true;
        createBackgroundEndMusic();
        SendHighScoreToServerAlert();

        Debug.d("Game Over");
    }

    private void checkHint() {
        Debug.d(String.format("Check Hint Count Down: %d", hintCountDown));
        hintCountDown--;
        hintCountDown = Math.max(0, hintCountDown);
        if (hintCountDown < 1)
            giveHint();

    }
    private void giveHint() {
        Debug.d(String.format("Looking for hint"));
        int makeWhat = 10;
        for (NumberBubble b: Bubbles) {
            for (NumberBubble bb: Bubbles) {
                if (bb == b) continue;
                if (bb.No+b.No == makeWhat) {
                    bb.setHint(true);
                    b.setHint(true);
                    return;
                }
                for (NumberBubble bbb: Bubbles) {
                    if (bbb == bb || bbb == b) continue;
                    if (bbb.No+bb.No+b.No == makeWhat) {
                        bbb.setHint(true);
                        bb.setHint(true);
                        b.setHint(true);
                        return;
                    }
                    for (NumberBubble bbbb: Bubbles) {
                        if (bbbb == bbb || bbbb == bb || bbbb == b) continue;
                        if (bbbb.No+bbb.No+bb.No+b.No == makeWhat) {
                            bbbb.setHint(true);
                            bbb.setHint(true);
                            bb.setHint(true);
                            b.setHint(true);
                            return;
                        }
                    }
                }
            }
        }
    }

    public void startGame()
    {
        onMenu = false;
        gameOver = false;

        removeMenu();
        createBucket();
        DrawScore();

        createSoundButton(isSoundOn);
        createPlayPauseButton(gamePaused);
        createGotoMenuButton();

        bubbleRadiusMinDivider = 4;
        bubbleRadiusDivider = 6;
        levelIntervalInSeconds = 3;
        score=0;
        nextLevelScore = 100;
        onScoreAction = false;
        hintsEnabled = true;
        hintCountDown = 5;

        Debug.d("Game started");
    }

    private void removeOutOfScreenBubbles()
    {
        float frameW = CAMERA_WIDTH;
        float frameH = CAMERA_HEIGHT;

        final ArrayList<NumberBubble> dI = new ArrayList<>();
        for (NumberBubble b: Bubbles) {
            if (b.getX() > frameW || b.getY() > frameH || b.getX() < 0 || b.getY() < 0) {
                dI.add(b);
            }
        }

        if (dI.size()<1)
            return;

        Debug.d(String.format("Out of screen bubbles detected count : %d", dI.size()));

        Bubbles.removeAll(dI);
        for (NumberBubble b : dI) {
            b.setIgnoreUpdate(true);
            mScene.detachChild(b);
            mPhysicsWorld.destroyBody(b.Body);
            b.dispose();
        }
    }

    private void removeBucket()
    {
        mScene.detachChild(ground);
        mScene.detachChild(left);
        mScene.detachChild(right);

        Debug.d("Bucket Removed");
    }

    private void removeMenu()
    {
        mScene.detachChild(playButton);
        mScene.detachChild(infoButton);
        mScene.detachChild(highScoreButton);
        mScene.detachChild(reviewButton);

        Debug.d("Menu removed");

        /*
        final FadeOutModifier m1 = new FadeOutModifier(0.3f) {
            @Override
            protected void onModifierFinished(IEntity pItem) {
                super.onModifierFinished(pItem);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //playButton.dispose();
                        playButton.setIgnoreUpdate(true);
                        mScene.detachChild(playButton);
                    }
                });
                final FadeOutModifier m2 = new FadeOutModifier(0.3f) {
                    @Override
                    protected void onModifierFinished(IEntity pItem) {
                        super.onModifierFinished(pItem);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                infoButton.setIgnoreUpdate(true);
                                mScene.detachChild(infoButton);
                            }
                        });
                        final FadeOutModifier m3 = new FadeOutModifier(0.3f) {
                            @Override
                            protected void onModifierFinished(IEntity pItem) {
                                super.onModifierFinished(pItem);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        highScoreButton.setIgnoreUpdate(true);
                                        mScene.detachChild(highScoreButton);
                                    }
                                });
                                final FadeOutModifier m4 = new FadeOutModifier(0.3f) {
                                    @Override
                                    protected void onModifierFinished(IEntity pItem) {
                                        super.onModifierFinished(pItem);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                reviewButton.setIgnoreUpdate(true);
                                                mScene.detachChild(reviewButton);
                                            }
                                        });
                                    }
                                };
                                reviewButton.registerEntityModifier(m4);
                            }
                        };
                        highScoreButton.registerEntityModifier(m3);
                    }
                };
                infoButton.registerEntityModifier(m2);
            }
        };
        playButton.registerEntityModifier(m1);
        */

        mScene.detachChild(exitButton);
    }

    private void gotoMenu()
    {
        createBackgroundMusic();
        createMenu();
        removeBucket();

        mScene.detachChild(gotoMenuButton);
        mScene.detachChild(soundButton);
        mScene.detachChild(playPauseButton);
    }

    private void createMenu()
    {
        onMenu = true;
        float frameW = CAMERA_WIDTH;
        float frameH = CAMERA_HEIGHT;
        float menuBtnW = frameW / 4;
        float menuBtnH = menuBtnW * MenuButton.buttonScaleRatio();

        playButton = new MenuButton(frameW/2 - menuBtnW/2,
                frameH/2 - menuBtnH*1.1f*1.5f - menuBtnH/2,
                MenuButton.PlayButtonTextureRegion,
                getVertexBufferObjectManager())
        {
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    startGame();
                    return true;
                }
                else
                    return false;
            }
        };
        playButton.setSize(menuBtnW, menuBtnH);
        playButton.setZIndex(9);
        mScene.registerTouchArea(playButton);
        mScene.setTouchAreaBindingOnActionDownEnabled(true);
        mScene.attachChild(playButton);

        infoButton = new MenuButton(frameW/2 - menuBtnW/2,
                frameH/2 - menuBtnH*1.1f*0.5f - menuBtnH/2,
                MenuButton.InfoButtonTextureRegion,
                getVertexBufferObjectManager())
        {
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    gotoHowToPlay();
                    return true;
                }
                else
                    return false;
            }
        };
        infoButton.setSize(menuBtnW, menuBtnH);
        infoButton.setZIndex(9);
        mScene.attachChild(infoButton);

        highScoreButton = new MenuButton(frameW/2 - menuBtnW/2,
                frameH/2 + menuBtnH*1.1f*0.5f - menuBtnH/2,
                MenuButton.HighScoreButtonTextureRegion,
                getVertexBufferObjectManager())
        {
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    gotoHighScores();
                    return true;
                }
                else
                    return false;
            }
        };
        highScoreButton.setSize(menuBtnW, menuBtnH);
        highScoreButton.setZIndex(9);
        mScene.attachChild(highScoreButton);

        reviewButton = new MenuButton(frameW/2 - menuBtnW/2,
                frameH/2 + menuBtnH*1.1f*1.5f - menuBtnH/2,
                MenuButton.ReviewButtonTextureRegion,
                getVertexBufferObjectManager())
        {
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    gotoRates();
                    return true;
                }
                else
                    return false;
            }
        };
        reviewButton.setSize(menuBtnW, menuBtnH);
        reviewButton.setZIndex(9);
        mScene.attachChild(reviewButton);

        exitButton = new MenuButton(frameW-xMargin-menuBtnW/2/2-menuBtnW/2,
                yMargin+menuBtnH/2/2-menuBtnH/2,
                MenuButton.ExitButtonTextureRegion,
                getVertexBufferObjectManager())
        {
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    doExit();
                    return true;
                }
                else
                    return false;
            }
        };
        exitButton.setSize(menuBtnW / 2, menuBtnH / 2);
        exitButton.setZIndex(9);
        mScene.attachChild(exitButton);

        //Animations
        playButton.setAlpha(0);
        infoButton.setAlpha(0);
        highScoreButton.setAlpha(0);
        reviewButton.setAlpha(0);

        Debug.d("Create menu started");
        FadeInModifier m1 = new FadeInModifier(1) {
            @Override
            protected void onModifierFinished(IEntity pItem) {
                super.onModifierFinished(pItem);
                FadeInModifier m1 = new FadeInModifier(1){
                    @Override
                    protected void onModifierFinished(IEntity pItem) {
                        super.onModifierFinished(pItem);
                        FadeInModifier m1 = new FadeInModifier(1){
                            @Override
                            protected void onModifierFinished(IEntity pItem) {
                                super.onModifierFinished(pItem);
                                FadeInModifier m1 = new FadeInModifier(1){
                                    @Override
                                    protected void onModifierFinished(IEntity pItem) {
                                        super.onModifierFinished(pItem);
                                        Debug.d("Menu Create finished");
                                    }
                                };
                                reviewButton.registerEntityModifier(m1);
                            }
                        };
                        highScoreButton.registerEntityModifier(m1);
                    }
                };
                infoButton.registerEntityModifier(m1);
            }
        };
        playButton.registerEntityModifier(m1);
   }

    private void createBackground() {
        mScene.setBackground(new Background(0.9f, 0.9f, 1f));
        float frameW = CAMERA_WIDTH;
        float frameH = CAMERA_HEIGHT;
        Sprite background = new Sprite(0,0,backgroundTextureRegion,getVertexBufferObjectManager());
        background.setSize(frameW, frameH);
        background.setZIndex(-1);
        mScene.attachChild(background);
        Debug.d("Background created");
    }

    private void createSoundButton(boolean isSoundOn){
        //TODO: createSoundButton
    }

    private void createPlayPauseButton(boolean gamePaused){
        //TODO: createPlayPauseButton
    }
    private void createGotoMenuButton() {
        //TODO: createGotoMenuButton
    }

    private void createBackgroundMusic(){
        //TODO: Create Background Music
    }

    private void createBackgroundEndMusic(){
        //TODO: Create Background End Music
    }

    private void SendHighScoreToServerAlert() {
        System.out.printf("Sending High Score To Server Dialog, Score : %d%n", score);
        final Activity mainActivity = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final EditText input = new EditText(mainActivity);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                new AlertDialog.Builder(MainActivity.Instance)
                        .setView(input)
                        .setTitle("Congratulations")
                        .setCancelable(false)
                        .setMessage(String.format("Great Score: %d. Enter your name to the high score table", score))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                UserName = input.getText().toString();
                                System.out.printf("Username selected: %s%n", UserName);
                                SendHighScoreToServer();
                                gotoMenu();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                gotoMenu();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
    }

    private void SendHighScoreToServer() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                String deviceId = android_id;
                UserName = new String(UserName.getBytes(Charset.forName("UTF-8")));
                String url = String.format(
                        "%s/HighScore?appId=%s&deviceId=%s&name=%s&score=%d",
                        Constants.ApiAddress, Constants.AppId, deviceId, UserName, score);

                System.out.println(String.format("Send High Score To Server Score :%d Device Id: %s Username: %s%n", score, deviceId, UserName));
                performPostCall(url, new HashMap<String, String>());
            }
        };
        task.run();
        postToSocial();
    }

    private String performPostCall(String requestURL, HashMap<String, String> postDataParams) {
        URL url;
        String response = "";

        System.out.printf("Perform HttpPost: %s%n", requestURL);

        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {
                response = "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.printf("Http Post Response : %s%n", response);
        return response;
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        if (params == null)
            return null;

        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    private void postToSocial() {
        /*
        Bitmap bmp = screenShot();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 90, stream);
        byte[] bmpByteArray = stream.toByteArray();

        File f = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg");
        try {
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bmpByteArray);
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
        final String filePath = Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg";
        Debug.d(String.format("Screen Capturing at %s", filePath));
        ScreenCapture screenCapture = new ScreenCapture();
        screenCapture.capture(CAMERA_WIDTH, CAMERA_HEIGHT, filePath, new ScreenCapture.IScreenCaptureCallback() {
            @Override
            public void onScreenCaptured(String pFilePath) {
                Debug.d(String.format("Screen Captured at %s, Intending Share.", pFilePath));
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/jpeg"); // might be text, sound, whatever
                //share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/temporary_file.jpg"));
                share.putExtra(Intent.EXTRA_STREAM, Uri.parse(pFilePath));
                share.putExtra(Intent.EXTRA_TEXT, String.format("Hey, I completed #%s with score %d %s", Constants.GameName, score, Constants.WebSite));
                //share.putExtra(Intent.EXTRA_TEXT, Constants.WebSite);
                //share.putExtra(Intent.EXTRA_HTML_TEXT, String.format("<a href='%s'>%s</a>", Constants.WebSite, Constants.WebSite));
                startActivity(Intent.createChooser(share, "Share Your Score"));
            }

            @Override
            public void onScreenCaptureFailed(String pFilePath, Exception pException) {
                Debug.d("Screen Capture Failed, ");
                pException.printStackTrace();
            }
        });
    }

    private void doExit() {
        new AlertDialog.Builder(this)
                .setTitle("Leaving")
                .setMessage("Do you want to leave?")
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        (MainActivity.Instance).finish();
                        System.exit(0);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void gotoHighScores() {
        String link = Constants.WebSite + "/HighScore";
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        this.startActivity(browserIntent);
    }

    private void gotoRates() {
        String link = Constants.GoogleAppLink;
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        this.startActivity(browserIntent);
    }

    private void gotoHowToPlay() {

    }

    /*
    private Bitmap screenShot() {
        Bitmap b = Bitmap.createBitmap( getWidth(), getHeight(), Bitmap.Config.RGB_565);
        Canvas c = new Canvas(b);
        ScreenCapture s = new ScreenCapture();
        s.capture(CAMERA_WIDTH,CAMERA_HEIGHT,);
        return b;
    }
    */
}