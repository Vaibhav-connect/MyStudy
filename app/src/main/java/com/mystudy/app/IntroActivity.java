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
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class IntroActivity extends AppCompatActivity {

    private TextView boy;
    private TextView book;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createIntroScreen();
        startAnimation();
    }

    private void createIntroScreen() {

        LinearLayout root = new LinearLayout(this);

        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(30, 30, 30, 30);

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

        root.addView(title);

        TextView tagline = new TextView(this);
        tagline.setText("Learn • Practice • Grow");
        tagline.setTextSize(18);
        tagline.setTextColor(Color.WHITE);
        tagline.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams taglineParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        taglineParams.topMargin = 8;
        root.addView(tagline, taglineParams);

        LinearLayout scene = new LinearLayout(this);
        scene.setGravity(Gravity.CENTER_VERTICAL);
        scene.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams sceneParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        300
                );

        sceneParams.topMargin = 60;
        root.addView(scene, sceneParams);

        boy = new TextView(this);
        boy.setText("👦");
        boy.setTextSize(72);
        boy.setGravity(Gravity.CENTER);

        scene.addView(
                boy,
                new LinearLayout.LayoutParams(130, 150)
        );

        book = new TextView(this);
        book.setText("📕");
        book.setTextSize(80);
        book.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams bookParams =
                new LinearLayout.LayoutParams(150, 150);

        bookParams.leftMargin = 100;

        scene.addView(book, bookParams);

        TextView welcome = new TextView(this);
        welcome.setText("Welcome to MyStudy! ✨");
        welcome.setTextSize(20);
        welcome.setTextColor(Color.WHITE);
        welcome.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams welcomeParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        welcomeParams.topMargin = 35;
        root.addView(welcome, welcomeParams);

        setContentView(root);
    }

    private void startAnimation() {

        boy.post(() -> {

            float startX = -700f;
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
                            -12f,
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

                            book.setText("📖");

                            AnimatorSet bookAnimation =
                                    new AnimatorSet();

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

                                            openLoginScreen();
                                        }
                                    }
                            );

                            bookAnimation.start();
                        }
                    }
            );

            AnimatorSet walkingAnimation =
                    new AnimatorSet();

            walkingAnimation.playTogether(
                    walk,
                    bob
            );

            walkingAnimation.start();
        });
    }

    private void openLoginScreen() {

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
}
