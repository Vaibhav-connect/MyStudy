package com.mystudy.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class IntroActivity extends AppCompatActivity {

    private FrameLayout root;

    private ImageView welcomeImage;
    private ImageView boyImage;
    private ImageView bookThrowImage;
    private ImageView bookGlowImage;
    private ImageView bookOpenImage;

    private TextView title;
    private TextView subtitle;

    private AnimatorSet currentAnimation;

    private boolean openingLogin = false;

    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createIntroUI();

        root.post(() -> {
            if (isScreenActive()) {
                startIntroAnimation();
            }
        });
    }

    private boolean isScreenActive() {
        return !isFinishing() && !isDestroyed();
    }

    private int dp(float value) {
        return (int) (
                value * getResources()
                        .getDisplayMetrics()
                        .density + 0.5f
        );
    }

    private void createIntroUI() {

        root = new FrameLayout(this);

        GradientDrawable background =
                new GradientDrawable(
                        GradientDrawable.Orientation.TL_BR,
                        new int[]{
                                Color.rgb(29, 78, 216),
                                Color.rgb(79, 70, 229),
                                Color.rgb(124, 58, 237)
                        }
                );

        root.setBackground(background);

        // ---------------------------------------
        // WELCOME IMAGE
        // ---------------------------------------

        welcomeImage = createImageView(
                R.drawable.intro_welcome
        );

        addFullScreen(welcomeImage);

        // ---------------------------------------
        // BOY
        // ---------------------------------------

        boyImage = createImageView(
                R.drawable.intro_boy_enter
        );

        FrameLayout.LayoutParams boyParams =
                new FrameLayout.LayoutParams(
                        dp(300),
                        dp(430)
                );

        boyParams.gravity = Gravity.CENTER;

        boyImage.setLayoutParams(boyParams);

        root.addView(boyImage);

        // ---------------------------------------
        // BOOK THROW
        // ---------------------------------------

        bookThrowImage = createImageView(
                R.drawable.intro_book_throw
        );

        FrameLayout.LayoutParams throwParams =
                new FrameLayout.LayoutParams(
                        dp(300),
                        dp(300)
                );

        throwParams.gravity = Gravity.CENTER;

        bookThrowImage.setLayoutParams(throwParams);

        root.addView(bookThrowImage);

        // ---------------------------------------
        // BOOK GLOW
        // ---------------------------------------

        bookGlowImage = createImageView(
                R.drawable.intro_book_glow
        );

        FrameLayout.LayoutParams glowParams =
                new FrameLayout.LayoutParams(
                        dp(330),
                        dp(330)
                );

        glowParams.gravity = Gravity.CENTER;

        bookGlowImage.setLayoutParams(glowParams);

        root.addView(bookGlowImage);

        // ---------------------------------------
        // BOOK OPEN
        // ---------------------------------------

        bookOpenImage = createImageView(
                R.drawable.intro_book_open
        );

        FrameLayout.LayoutParams openParams =
                new FrameLayout.LayoutParams(
                        dp(360),
                        dp(360)
                );

        openParams.gravity = Gravity.CENTER;

        bookOpenImage.setLayoutParams(openParams);

        root.addView(bookOpenImage);

        // ---------------------------------------
        // TITLE
        // ---------------------------------------

        LinearLayout textContainer =
                new LinearLayout(this);

        textContainer.setOrientation(
                LinearLayout.VERTICAL
        );

        textContainer.setGravity(Gravity.CENTER);

        FrameLayout.LayoutParams textParams =
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        textParams.gravity = Gravity.TOP;
        textParams.topMargin = dp(65);

        textContainer.setLayoutParams(textParams);

        title = new TextView(this);

        title.setText("MyStudy");
        title.setTextColor(Color.WHITE);
        title.setTextSize(38);
        title.setGravity(Gravity.CENTER);
        title.setTypeface(null, android.graphics.Typeface.BOLD);

        textContainer.addView(title);

        subtitle = new TextView(this);

        subtitle.setText("Learn • Practice • Grow");
        subtitle.setTextColor(
                Color.argb(230, 255, 255, 255)
        );
        subtitle.setTextSize(17);
        subtitle.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams subtitleParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        subtitleParams.topMargin = dp(6);

        textContainer.addView(
                subtitle,
                subtitleParams
        );

        root.addView(textContainer);

        // ---------------------------------------
        // INITIAL STATES
        // ---------------------------------------

        welcomeImage.setAlpha(0f);

        boyImage.setAlpha(0f);
        boyImage.setTranslationX(-dp(500));

        bookThrowImage.setAlpha(0f);
        bookThrowImage.setScaleX(0.4f);
        bookThrowImage.setScaleY(0.4f);
        bookThrowImage.setRotation(-20f);

        bookGlowImage.setAlpha(0f);
        bookGlowImage.setScaleX(0.5f);
        bookGlowImage.setScaleY(0.5f);

        bookOpenImage.setAlpha(0f);
        bookOpenImage.setScaleX(0.4f);
        bookOpenImage.setScaleY(0.4f);
        bookOpenImage.setRotationY(80f);

        title.setAlpha(0f);
        title.setTranslationY(-dp(25));

        subtitle.setAlpha(0f);
        subtitle.setTranslationY(dp(15));

        setContentView(root);
    }

    private ImageView createImageView(int drawableId) {

        ImageView image =
                new ImageView(this);

        image.setImageResource(drawableId);

        image.setScaleType(
                ImageView.ScaleType.CENTER_INSIDE
        );

        image.setAdjustViewBounds(true);

        return image;
    }

    private void addFullScreen(View view) {

        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                );

        params.gravity = Gravity.CENTER;

        root.addView(view, params);
    }

    private void startIntroAnimation() {

        // ---------------------------------------
        // 1. WELCOME
        // ---------------------------------------

        AnimatorSet welcomeAnimation =
                new AnimatorSet();

        ObjectAnimator welcomeFade =
                ObjectAnimator.ofFloat(
                        welcomeImage,
                        View.ALPHA,
                        0f,
                        1f
                );

        welcomeFade.setDuration(700);

        ObjectAnimator titleFade =
                ObjectAnimator.ofFloat(
                        title,
                        View.ALPHA,
                        0f,
                        1f
                );

        ObjectAnimator titleMove =
                ObjectAnimator.ofFloat(
                        title,
                        View.TRANSLATION_Y,
                        -dp(25),
                        0f
                );

        titleFade.setDuration(650);
        titleMove.setDuration(650);

        ObjectAnimator subtitleFade =
                ObjectAnimator.ofFloat(
                        subtitle,
                        View.ALPHA,
                        0f,
                        1f
                );

        ObjectAnimator subtitleMove =
                ObjectAnimator.ofFloat(
                        subtitle,
                        View.TRANSLATION_Y,
                        dp(15),
                        0f
                );

        subtitleFade.setDuration(600);
        subtitleMove.setDuration(600);

        welcomeAnimation.playTogether(
                welcomeFade,
                titleFade,
                titleMove,
                subtitleFade,
                subtitleMove
        );

        welcomeAnimation.setInterpolator(
                new DecelerateInterpolator()
        );

        welcomeAnimation.addListener(
                new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationEnd(
                            Animator animation
                    ) {

                        if (!isScreenActive()) {
                            return;
                        }

                        handler.postDelayed(
                                () -> startBoyEntry(),
                                450
                        );
                    }
                }
        );

        currentAnimation = welcomeAnimation;

        welcomeAnimation.start();
    }

    // ---------------------------------------
    // 2. BOY ENTERS
    // ---------------------------------------

    private void startBoyEntry() {

        if (!isScreenActive()) {
            return;
        }

        boyImage.setAlpha(1f);

        ObjectAnimator walk =
                ObjectAnimator.ofFloat(
                        boyImage,
                        View.TRANSLATION_X,
                        -dp(500),
                        dp(10),
                        0f
                );

        walk.setDuration(1900);

        walk.setInterpolator(
                new AccelerateDecelerateInterpolator()
        );

        ObjectAnimator bodyBounce =
                ObjectAnimator.ofFloat(
                        boyImage,
                        View.TRANSLATION_Y,
                        0f,
                        -dp(8),
                        0f,
                        -dp(6),
                        0f
                );

        bodyBounce.setDuration(1900);

        ObjectAnimator scale =
                ObjectAnimator.ofFloat(
                        boyImage,
                        View.SCALE_X,
                        0.90f,
                        1f
                );

        ObjectAnimator scaleY =
                ObjectAnimator.ofFloat(
                        boyImage,
                        View.SCALE_Y,
                        0.90f,
                        1f
                );

        scale.setDuration(900);
        scaleY.setDuration(900);

        AnimatorSet boyAnimation =
                new AnimatorSet();

        boyAnimation.playTogether(
                walk,
                bodyBounce,
                scale,
                scaleY
        );

        boyAnimation.addListener(
                new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationEnd(
                            Animator animation
                    ) {

                        if (!isScreenActive()) {
                            return;
                        }

                        handler.postDelayed(
                                () -> startBookThrow(),
                                300
                        );
                    }
                }
        );

        currentAnimation = boyAnimation;

        boyAnimation.start();
    }

    // ---------------------------------------
    // 3. BOOK THROW / LAND
    // ---------------------------------------

    private void startBookThrow() {

        if (!isScreenActive()) {
            return;
        }

        bookThrowImage.setAlpha(1f);

        bookThrowImage.setTranslationX(
                -dp(90)
        );

        bookThrowImage.setTranslationY(
                dp(90)
        );

        ObjectAnimator moveX =
                ObjectAnimator.ofFloat(
                        bookThrowImage,
                        View.TRANSLATION_X,
                        -dp(90),
                        0f
                );

        ObjectAnimator moveY =
                ObjectAnimator.ofFloat(
                        bookThrowImage,
                        View.TRANSLATION_Y,
                        dp(90),
                        0f
                );

        ObjectAnimator rotate =
                ObjectAnimator.ofFloat(
                        bookThrowImage,
                        View.ROTATION,
                        -20f,
                        8f,
                        -3f,
                        0f
                );

        ObjectAnimator scale =
                ObjectAnimator.ofFloat(
                        bookThrowImage,
                        View.SCALE_X,
                        0.55f,
                        1.08f,
                        1f
                );

        ObjectAnimator scaleY =
                ObjectAnimator.ofFloat(
                        bookThrowImage,
                        View.SCALE_Y,
                        0.55f,
                        1.08f,
                        1f
                );

        moveX.setDuration(850);
        moveY.setDuration(850);
        rotate.setDuration(850);
        scale.setDuration(850);
        scaleY.setDuration(850);

        AnimatorSet bookAnimation =
                new AnimatorSet();

        bookAnimation.playTogether(
                moveX,
                moveY,
                rotate,
                scale,
                scaleY
        );

        bookAnimation.setInterpolator(
                new OvershootInterpolator(1.2f)
        );

        bookAnimation.addListener(
                new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationEnd(
                            Animator animation
                    ) {

                        if (!isScreenActive()) {
                            return;
                        }

                        startBookGlow();
                    }
                }
        );

        currentAnimation = bookAnimation;

        bookAnimation.start();
    }

    // ---------------------------------------
    // 4. GLOW
    // ---------------------------------------

    private void startBookGlow() {

        if (!isScreenActive()) {
            return;
        }

        bookGlowImage.setAlpha(0f);
        bookGlowImage.setScaleX(0.5f);
        bookGlowImage.setScaleY(0.5f);

        AnimatorSet glowAnimation =
                new AnimatorSet();

        ObjectAnimator fade =
                ObjectAnimator.ofFloat(
                        bookGlowImage,
                        View.ALPHA,
                        0f,
                        1f,
                        0.55f,
                        1f
                );

        ObjectAnimator scale =
                ObjectAnimator.ofFloat(
                        bookGlowImage,
                        View.SCALE_X,
                        0.5f,
                        1.15f,
                        0.95f,
                        1.08f
                );

        ObjectAnimator scaleY =
                ObjectAnimator.ofFloat(
                        bookGlowImage,
                        View.SCALE_Y,
                        0.5f,
                        1.15f,
                        0.95f,
                        1.08f
                );

        fade.setDuration(1200);
        scale.setDuration(1200);
        scaleY.setDuration(1200);

        glowAnimation.playTogether(
                fade,
                scale,
                scaleY
        );

        glowAnimation.addListener(
                new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationEnd(
                            Animator animation
                    ) {

                        if (!isScreenActive()) {
                            return;
                        }

                        handler.postDelayed(
                                () -> startBookOpen(),
                                150
                        );
                    }
                }
        );

        currentAnimation = glowAnimation;

        glowAnimation.start();
    }

    // ---------------------------------------
    // 5. BOOK OPENS
    // ---------------------------------------

    private void startBookOpen() {

        if (!isScreenActive()) {
            return;
        }

        bookThrowImage.animate()
                .alpha(0f)
                .setDuration(300)
                .start();

        bookGlowImage.animate()
                .alpha(0f)
                .setDuration(500)
                .start();

        bookOpenImage.setAlpha(1f);

        ObjectAnimator rotation =
                ObjectAnimator.ofFloat(
                        bookOpenImage,
                        View.ROTATION_Y,
                        80f,
                        0f
                );

        ObjectAnimator scale =
                ObjectAnimator.ofFloat(
                        bookOpenImage,
                        View.SCALE_X,
                        0.4f,
                        1.15f,
                        1f
                );

        ObjectAnimator scaleY =
                ObjectAnimator.ofFloat(
                        bookOpenImage,
                        View.SCALE_Y,
                        0.4f,
                        1.15f,
                        1f
                );

        ObjectAnimator alpha =
                ObjectAnimator.ofFloat(
                        bookOpenImage,
                        View.ALPHA,
                        0f,
                        1f
                );

        rotation.setDuration(900);
        scale.setDuration(900);
        scaleY.setDuration(900);
        alpha.setDuration(600);

        AnimatorSet openAnimation =
                new AnimatorSet();

        openAnimation.playTogether(
                rotation,
                scale,
                scaleY,
                alpha
        );

        openAnimation.setInterpolator(
                new DecelerateInterpolator()
        );

        openAnimation.addListener(
                new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationEnd(
                            Animator animation
                    ) {

                        if (!isScreenActive()) {
                            return;
                        }

                        handler.postDelayed(
                                () -> openLoginScreen(),
                                450
                        );
                    }
                }
        );

        currentAnimation = openAnimation;

        openAnimation.start();
    }

    // ---------------------------------------
    // 6. LOGIN REVEAL
    // ---------------------------------------

    private void openLoginScreen() {

        if (!isScreenActive() || openingLogin) {
            return;
        }

        openingLogin = true;

        Intent intent =
                new Intent(
                        IntroActivity.this,
                        LoginActivity.class
                );

        startActivity(intent);

        overridePendingTransition(
                android.R.anim.fade_in,
                android.R.anim.fade_out
        );

        finish();
    }

    @Override
    protected void onDestroy() {

        if (currentAnimation != null) {
            currentAnimation.cancel();
            currentAnimation = null;
        }

        handler.removeCallbacksAndMessages(null);

        if (welcomeImage != null) {
            welcomeImage.animate().cancel();
        }

        if (boyImage != null) {
            boyImage.animate().cancel();
        }

        if (bookThrowImage != null) {
            bookThrowImage.animate().cancel();
        }

        if (bookGlowImage != null) {
            bookGlowImage.animate().cancel();
        }

        if (bookOpenImage != null) {
            bookOpenImage.animate().cancel();
        }

        super.onDestroy();
    }
}
