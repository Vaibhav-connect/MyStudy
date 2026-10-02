```java
package com.mystudy.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class IntroActivity extends AppCompatActivity {

    private TextView boy;
    private TextView book;
    private TextView title;

    private AnimatorSet walkingAnimation;
    private AnimatorSet bookAnimation;

    private boolean screenOpening = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createIntroScreen();
        startAnimation();
    }

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private void createIntroScreen() {

        LinearLayout root = new LinearLayout(this);

        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(dp(24), dp(24), dp(24), dp(24));

        GradientDrawable background = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{
                        Color.rgb(79, 70, 229),
                        Color.rgb(124, 58, 237),
                        Color.rgb(37, 99, 235)
                }
        );

        root.setBackground(background);

        title = new TextView(this);
        title.setText("MyStudy");
        title.setTextSize(40);
        title.setTextColor(Color.WHITE);
        title.setGravity(Gravity.CENTER);

        root.addView(
                title,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        );

        TextView tagline = new TextView(this);
        tagline.setText("Learn • Practice • Grow");
        tagline.setTextSize(18);
        tagline.setTextColor(Color.WHITE);
        tagline.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams taglineParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        taglineParams.topMargin = dp(8);

        root.addView(tagline, taglineParams);

        LinearLayout scene = new LinearLayout(this);

        scene.setGravity(Gravity.CENTER);
        scene.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams sceneParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(220)
                );

        sceneParams.topMargin = dp(35);

        root.addView(scene, sceneParams);

        boy = new TextView(this);
        boy.setText("👦");
        boy.setTextSize(64);
        boy.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams boyParams =
                new LinearLayout.LayoutParams(
                        dp(100),
                        dp(130)
                );

        scene.addView(boy, boyParams);

        book = new TextView(this);
        book.setText("📕");
        book.setTextSize(72);
        book.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams bookParams =
                new LinearLayout.LayoutParams(
                        dp(110),
                        dp(130)
                );

        bookParams.leftMargin = dp(35);

        scene.addView(book, bookParams);

        TextView welcome = new TextView(this);
        welcome.setText("Welcome to MyStudy! ✨");
        welcome.setTextSize(20);
        welcome.setTextColor(Color.WHITE);
        welcome.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams welcomeParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        welcomeParams.topMargin = dp(20);

        root.addView(welcome, welcomeParams);

        setContentView(root);
    }

    private void startAnimation() {

        boy.post(() -> {

            if (isFinishing() || isDestroyed()) {
                return;
            }

            float startX = -dp(180);
            float endX = 0f;

            boy.setTranslationX(startX);

            ObjectAnimator walk =
                    ObjectAnimator.ofFloat(
                            boy,
                            View.TRANSLATION_X,
                            startX,
                            endX
                    );

            walk.setDuration(1600);

            walk.setInterpolator(
                    new AccelerateDecelerateInterpolator()
            );

            ObjectAnimator bob =
                    ObjectAnimator.ofFloat(
                            boy,
                            View.TRANSLATION_Y,
                            0f,
                            -dp(5),
                            0f
                    );

            bob.setDuration(320);
            bob.setRepeatCount(4);

            ObjectAnimator bookScaleX =
                    ObjectAnimator.ofFloat(
                            book,
                            View.SCALE_X,
                            0.7f,
                            1.15f,
                            1f
                    );

            ObjectAnimator bookScaleY =
                    ObjectAnimator.ofFloat(
                            book,
                            View.SCALE_Y,
                            0.7f,
                            1.15f,
                            1f
                    );

            ObjectAnimator bookRotation =
                    ObjectAnimator.ofFloat(
                            book,
                            View.ROTATION,
                            -8f,
                            8f,
                            0f
                    );

            bookScaleX.setDuration(900);
            bookScaleY.setDuration(900);
            bookRotation.setDuration(900);

            walk.addListener(
                    new AnimatorListenerAdapter() {

                        @Override
                        public void onAnimationEnd(Animator animation) {

                            if (isFinishing() || isDestroyed()) {
                                return;
                            }

                            book.setText("📖");

                            bookAnimation = new AnimatorSet();

                            bookAnimation.playTogether(
                                    bookScaleX,
                                    bookScaleY,
                                    bookRotation
                            );

                            bookAnimation.addListener(
                                    new AnimatorListenerAdapter() {

                                        @Override
                                        public void onAnimationEnd(
                                                Animator animation
                                        ) {

                                            if (isFinishing()
                                                    || isDestroyed()
                                                    || screenOpening) {
                                                return;
                                            }

                                            openLoginScreen();
                                        }
                                    }
                            );

                            bookAnimation.start();
                        }
                    }
            );

            walkingAnimation = new AnimatorSet();

            walkingAnimation.playTogether(
                    walk,
                    bob
            );

            walkingAnimation.start();
        });
    }

    private void openLoginScreen() {

        if (screenOpening || isFinishing() || isDestroyed()) {
            return;
        }

        screenOpening = true;

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

        if (walkingAnimation != null) {
            walkingAnimation.cancel();
            walkingAnimation = null;
        }

        if (bookAnimation != null) {
            bookAnimation.cancel();
            bookAnimation = null;
        }

        if (boy != null) {
            boy.animate().cancel();
        }

        if (book != null) {
            book.animate().cancel();
        }

        super.onDestroy();
    }
}
```
