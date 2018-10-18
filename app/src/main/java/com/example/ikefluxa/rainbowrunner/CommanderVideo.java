package com.example.ikefluxa.rainbowrunner;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

class CommanderVideo {
    Vector2 position,
            lastPosition;
    float width,
            height;
    boolean isTouchingBottom,
            isKicking;
    boolean isVisible, isAlive;
    private boolean isJumping,
            isSliding,
            isLaunching,
            kickKeyHeld,
            lastSliding,
            attractMode;
    private float jumpFrame,
            slideHeight,
            kickTimerMax,
            kickTimer;
    private int runFrame,
            totalRunFrames,
            runFrameTimer,
            runFrameIncrement,
            runFrameTimerMax;
    private double velocityY,
            heightScale;
    private Paint paint;

    CommanderVideo() {
        position = new Vector2((Screen.width / 250) * (125 - GameVals.obstacleSpawnInterval) + Constants.blockSize * 2, Constants.groundHeight - Constants.cmndrSize);
        lastPosition = position.Clone();
        width = Constants.blockSize * 4;
        height = Constants.cmndrSize;
        velocityY = 0;
        isVisible = true;
        isJumping = false;
        jumpFrame = 0;
        isSliding = false;
        lastSliding = false;
        slideHeight = Math.round(Constants.blockSize * 4.5);
        isKicking = false;
        kickTimer = 0;
        kickTimerMax = (float) (Constants.obstacleSpawnMin * 0.75);
        kickKeyHeld = false;
        isLaunching = false;
        heightScale = 11d / 15d; // pct of height that is body
        runFrame = 0;
        totalRunFrames = 7;
        runFrameTimer = 0;
        runFrameIncrement = 1;
        runFrameTimerMax = 2;
        isTouchingBottom = true;
        isAlive = true;
        attractMode = false;
        paint = new Paint();
    }
    CollisionRectObj CollisionRect() {
        return new CollisionRectObj(new Vector2(position.x, position.y), width, height);
    }
    CollisionRectObj KickRect() {
        return new CollisionRectObj(new Vector2(position.x + width, position.y + Constants.blockSize * 9), Constants.blockSize * 5, Constants.blockSize * 2);
    }
    void Reset() {
        height = Constants.cmndrSize;
        position.y = Constants.groundHeight - height;
        velocityY = 0;
        isVisible = true;
        isJumping = false;
        isSliding = false;
        lastSliding = false;
        isKicking = false;
        kickKeyHeld = false;
        isLaunching = false;
        runFrame = 0;
        runFrameTimer = 0;
        isTouchingBottom = false;
        isAlive = true;
    }
    private boolean IsRunning() {
        return  !isJumping &&
                !isSliding &&
                !isKicking &&
                !isLaunching;
    }
    private void UpdateRunFrame() {
        if (!IsRunning()){
            runFrame = 0;
            return;
        }

        runFrameTimer += runFrameIncrement;
        if (runFrameTimer >= runFrameTimerMax){
            runFrameTimer = 0;
            runFrame = (++runFrame) % totalRunFrames;
        }
    }
    void GroundCollision() {
        if (isTouchingBottom){
            velocityY = 0;
            isJumping = false;
            isLaunching = false;
        }
    }
    void Launch() {
        isTouchingBottom = false;
        velocityY =  Constants.launchVelocityY;
        isLaunching = true;
        position.y = Constants.groundHeight - height;
    }
    boolean CanLaunch() {
        return Constants.IsKeyPressed("launch") && !isLaunching && isTouchingBottom;
    }
    private void UpdateKickTimer() {
        if (kickTimer > 0){
            kickTimer--;
            if (kickTimer <= 0){
                isKicking = false;
            }
        }
    }
    void Kill() {
        isAlive = false;
        ResetSlidePosition();
    }
    private void ResetSlidePosition() {
        if (isSliding){
            position.y -= Constants.cmndrSize - slideHeight + GameVals.g;
            height = Constants.cmndrSize;
        }
    }
    void Update() {
        lastPosition = position.Clone();
        lastSliding = isSliding;
        if (!Constants.IsKeyDown("kick") && !isKicking){
            kickKeyHeld = false;
        }

        // launch, additional air time
        if (isLaunching){
            velocityY += Constants.launchVelocityY * Constants.launchScale;
        }

        // jump
        if (Constants.IsKeyPressed("jump") &&
                isTouchingBottom)
        {
            jumpFrame = 0;
            isTouchingBottom = false;
            velocityY = Constants.jumpVelocity;
            isJumping = true;
        }
        // additional air time on held jump descent
        if (Constants.IsKeyDown("jump") && velocityY > -GameVals.g && isJumping && !isLaunching){
            jumpFrame = 1;
            velocityY += Constants.jumpVelocity * Constants.jumpScale;
        } else if (isJumping && !Constants.IsKeyPressed("jump")){
            jumpFrame = 0;
        }

        // slide
        if (Constants.IsKeyDown("slide") && isTouchingBottom){
            isSliding = true;
            if (isKicking){
                isKicking = false;
            }
        } else {
            isSliding = false;
        }

        // kick
        if (Constants.IsKeyDown("kick") && !isSliding && kickTimer <= 0 && !kickKeyHeld){
            isKicking = true;
            kickTimer = kickTimerMax;
            kickKeyHeld = true;
        }

        UpdateRunFrame();

        // gravity
        velocityY += GameVals.g;
        position.y += velocityY;

        // slide position correction
        if (isSliding && !lastSliding){
            position.y += height - slideHeight;
            height = slideHeight;
        } else if (!isSliding && lastSliding){
            position.y -= Constants.cmndrSize - slideHeight + GameVals.g;
            height = Constants.cmndrSize;
        }

        // fell off the bottom of the screen, loss
        if (position.y + height / 2 > Screen.height){
            Constants.LoseGame();
        }

        isTouchingBottom = false;

        float properX = (Screen.width / 250) * (125 - GameVals.obstacleSpawnInterval) + width / 2;
        position.x -= (position.x - properX) / 10;

        UpdateKickTimer();
        Constants.CheckGroundCollision();
        if (attractMode && GameVals.gameState.equals(GameStates.menu)){
            runFrame = 4;
        }
    }
    private void DrawEyeSlot(Canvas canvas) {
        paint.setColor(Color.rgb(255, 255, 255));
        float x = position.x + Constants.blockSize * 2;
        float y = position.y + Constants.blockSize;
        canvas.drawRect(x, y, Constants.blockSize * 2 + x, Constants.blockSize + y, paint);
    }
    void Draw(Canvas canvas) {
        if (!isVisible){return;}
        paint.setColor(Constants.black);
        float h = (float) (height * heightScale);
        float w2, h2, x, y;

        if (!isAlive){
            // left arm
            x = position.x - Constants.blockSize;
            y = position.y + Constants.blockSize * 8;
            canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);
            x -= Constants.blockSize;
            y += Constants.blockSize;
            canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);
            x -= Constants.blockSize;
            y += Constants.blockSize;
            canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);

            // right arm
            x = position.x + width;
            y = position.y + Constants.blockSize * 8;
            canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);
            x += Constants.blockSize;
            y += Constants.blockSize;
            canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize * 2 + y, paint);

            // left leg
            x = position.x + Constants.blockSize;
            y = position.y + h;
            canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);
            x -= Constants.blockSize;
            y += Constants.blockSize;
            canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);
            x -= Constants.blockSize;
            y += Constants.blockSize;
            canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);
            y += Constants.blockSize;
            canvas.drawRect(x, y, Constants.blockSize * 2 + x, Constants.blockSize + y, paint);

            // right leg
            x = position.x + width - Constants.blockSize * 2;
            y = position.y + h;
            canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);
            x += Constants.blockSize;
            y += Constants.blockSize;
            canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);
            x += Constants.blockSize;
            y += Constants.blockSize;
            canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);
            x -= Constants.blockSize;
            y += Constants.blockSize;
            canvas.drawRect(x, y, Constants.blockSize * 2 + x, Constants.blockSize + y, paint);

            // body
            canvas.drawRect(position.x, position.y, width + position.x, h + position.y, paint);

            // eye slot
            DrawEyeSlot(canvas);
        }
        else if (isSliding){
            w2 = Constants.blockSize * 5;
            h2 = height - Constants.blockSize * 2;
            // left arm
            x = position.x - Constants.blockSize;
            y = position.y + height - Constants.blockSize;
            canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);
            x -= Constants.blockSize;
            y -= Constants.blockSize*2;
            canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize * 2 + y, paint);

            // right arm
            x = position.x + width + Constants.blockSize * 2;
            y = position.y;
            canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);
            x -= Constants.blockSize;
            y += Constants.blockSize;
            canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);

            // head
            y = position.y;
            canvas.drawRect(position.x, y, width + position.x, height + y, paint);

            // body
            x = position.x + width;
            y = position.y + height - h2;
            canvas.drawRect(x, y, w2 + x, h2 + y, paint);

            // left leg (upper)
            x = position.x + width + w2;
            y = position.y + height - h2 - Constants.blockSize;
            canvas.drawRect(x, y, Constants.blockSize * 3 + x, Constants.blockSize + y, paint);
            x += Constants.blockSize * 2;
            y -= Constants.blockSize;
            canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);

            // right leg
            x = position.x + width + w2;
            y = position.y + height - Constants.blockSize;
            canvas.drawRect(x, y, Constants.blockSize * 5 + x, Constants.blockSize + y, paint);
            x += Constants.blockSize*4;
            y -= Constants.blockSize;
            canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);

            // eye slot
            DrawEyeSlot(canvas);
        }
        else if (isKicking) {
            // left arm
            x = position.x - Constants.blockSize * 3;
            y = position.y + Constants.blockSize * 6;
            canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);
            x += Constants.blockSize;
            y += Constants.blockSize;
            canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);
            x += Constants.blockSize;
            y += Constants.blockSize;
            canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);

            // right arm
            x += width + Constants.blockSize;
            canvas.drawRect(x, y, Constants.blockSize * 2 + x, Constants.blockSize + y, paint);

            // left leg
            x = position.x;
            y = position.y + h;
            canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);
            y += Constants.blockSize;
            canvas.drawRect(x, y, Constants.blockSize * 3 + x, Constants.blockSize + y, paint);

            // right leg
            x = position.x + width;
            y = position.y + h - Constants.blockSize;
            canvas.drawRect(x, y, Constants.blockSize * 5 + x, Constants.blockSize + y, paint);
            x += Constants.blockSize * 4;
            y -= Constants.blockSize;
            canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);

            // body
            canvas.drawRect(position.x, position.y, width + position.x, h + position.y, paint);

            // eye slot
            DrawEyeSlot(canvas);
        }
        else if (isLaunching){
            // left arm
            x = position.x - Constants.blockSize;
            y = position.y + height - Constants.blockSize * 9;
            canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);
            x -= Constants.blockSize;
            y += Constants.blockSize;
            canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);
            x -= Constants.blockSize;
            y += Constants.blockSize;
            canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);

            // right arm
            x = position.x + width;
            y = position.y + height  - Constants.blockSize * 12;
            canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);
            x += Constants.blockSize;
            y -= Constants.blockSize;
            canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);
            x += Constants.blockSize;
            y -= Constants.blockSize;
            canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);

            // left leg
            x = position.x - Constants.blockSize;
            y = position.y + h;
            canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);
            x -= Constants.blockSize;
            y += Constants.blockSize;
            canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);
            x -= Constants.blockSize;
            y += Constants.blockSize;
            canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);
            x -= Constants.blockSize;
            y += Constants.blockSize;
            canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);

            // right leg
            x = position.x + width - Constants.blockSize * 2;
            y = position.y + h;
            canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);
            x -= Constants.blockSize;
            y += Constants.blockSize;
            canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);
            x -= Constants.blockSize;
            y += Constants.blockSize;
            canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);

            // body
            canvas.drawRect(position.x, position.y, width + position.x, h + position.y, paint);

            // eye slot
            DrawEyeSlot(canvas);
        }
        else if (isJumping) {
            switch((int) jumpFrame){
                case 0:
                    // left arm
                    x = position.x - Constants.blockSize * 3;
                    y = position.y + height - Constants.blockSize * 8;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);
                    x += Constants.blockSize;
                    y -= Constants.blockSize;
                    canvas.drawRect(x, y, Constants.blockSize * 2 + x, Constants.blockSize + y, paint);

                    // right arm
                    x += width + Constants.blockSize * 2;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);
                    x += Constants.blockSize;
                    y += Constants.blockSize;
                    canvas.drawRect(x, y, Constants.blockSize * 2 + x, Constants.blockSize + y, paint);

                    // left leg
                    x = position.x;
                    y = position.y + h;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize * 3 + y, paint);
                    x -= Constants.blockSize;
                    y += Constants.blockSize * 3;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);

                    // right leg
                    x = position.x + width - Constants.blockSize;
                    y = position.y + h;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize * 3 + y, paint);
                    x -= Constants.blockSize;
                    y += Constants.blockSize * 3;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);

                    // body
                    canvas.drawRect(position.x, position.y, width + position.x, h + position.y, paint);

                    // eye slot
                    DrawEyeSlot(canvas);
                    break;
                case 1:
                    // left arm
                    x = position.x - Constants.blockSize * 3;
                    y = position.y + height - Constants.blockSize * 11;
                    canvas.drawRect(x, y, Constants.blockSize * 2 + x, Constants.blockSize + y, paint);
                    x += Constants.blockSize * 2;
                    y += Constants.blockSize;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);

                    // right arm
                    x += width + Constants.blockSize;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);
                    x += Constants.blockSize;
                    y += Constants.blockSize;
                    canvas.drawRect(x, y, Constants.blockSize * 2 + x, Constants.blockSize + y, paint);

                    // left leg
                    x = position.x + Constants.blockSize;
                    y = position.y + h;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize * 3 + y, paint);

                    // right leg
                    x = position.x + width - Constants.blockSize;
                    y = position.y + h;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize * 2 + y, paint);
                    x += Constants.blockSize;
                    y += Constants.blockSize*2;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize * 2 + y, paint);

                    // body
                    canvas.drawRect(position.x, position.y, width + position.x, h + position.y, paint);

                    // eye slot
                    DrawEyeSlot(canvas);
                    break;
            }
        }
        else if (IsRunning()){
            // debug frame
            switch(runFrame){
                case 0:
                    // right arm
                    x = position.x + width;
                    y = position.y + height - Constants.blockSize * 6;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);

                    // left leg
                    x = position.x + Constants.blockSize;
                    y = position.y + h;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize * 2 + y, paint);
                    x -= Constants.blockSize;
                    y += Constants.blockSize * 2;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize * 2 + y, paint);

                    // right leg
                    x = position.x + width - Constants.blockSize * 2;
                    y = position.y + h;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize * 4 + y, paint);
                    x += Constants.blockSize;
                    y += Constants.blockSize * 3;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);
                    break;
                case 1:
                    // left arm
                    x = position.x - Constants.blockSize;
                    y = position.y + Constants.blockSize * 5;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);

                    // left leg
                    x = position.x + Constants.blockSize;
                    y = position.y + h;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);
                    x -= Constants.blockSize;
                    y += Constants.blockSize;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);
                    x -= Constants.blockSize*2;
                    y += Constants.blockSize;
                    canvas.drawRect(x, y, Constants.blockSize * 2 + x, Constants.blockSize + y, paint);
                    y += Constants.blockSize;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);

                    // right leg
                    x = position.x + width - Constants.blockSize;
                    y = position.y + h;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);
                    x += Constants.blockSize;
                    y += Constants.blockSize;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);
                    x -= Constants.blockSize * 2;
                    y += Constants.blockSize;
                    canvas.drawRect(x, y, Constants.blockSize * 2 + x, Constants.blockSize + y, paint);
                    y += Constants.blockSize;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);
                    break;
                case 2:
                    // left arm
                    x = position.x - Constants.blockSize;
                    y = position.y + Constants.blockSize * 4;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);
                    x -= Constants.blockSize;
                    y += Constants.blockSize;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);
                    x += Constants.blockSize;
                    y += Constants.blockSize;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);

                    // right arm
                    x = position.x + width;
                    y += Constants.blockSize * 2;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);

                    // left leg
                    x = position.x - Constants.blockSize;
                    y = position.y + h;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);
                    x -= Constants.blockSize;
                    y += Constants.blockSize;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);
                    x -= Constants.blockSize;
                    y += Constants.blockSize;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);
                    x += Constants.blockSize;
                    y += Constants.blockSize;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);

                    // right leg
                    x = position.x + width;
                    y = position.y + h;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);
                    x += Constants.blockSize;
                    y += Constants.blockSize;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);
                    y += Constants.blockSize;
                    canvas.drawRect(x, y, Constants.blockSize * 2 + x, Constants.blockSize + y, paint);
                    break;
                case 3:
                    // left arm
                    x = position.x - Constants.blockSize;
                    y = position.y + Constants.blockSize * 2;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);
                    x -= Constants.blockSize;
                    y += Constants.blockSize;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);
                    x -= Constants.blockSize;
                    y += Constants.blockSize;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);

                    // right arm
                    x = position.x + width;
                    y = position.y + Constants.blockSize * 6;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);
                    x += Constants.blockSize;
                    y -= Constants.blockSize;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);
                    x += Constants.blockSize;
                    y -= Constants.blockSize;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);

                    // left leg
                    x = position.x - Constants.blockSize * 3;
                    y = position.y + h - Constants.blockSize;
                    canvas.drawRect(x, y, Constants.blockSize * 3 + x, Constants.blockSize + y, paint);
                    x -= Constants.blockSize;
                    y += Constants.blockSize;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);

                    // right leg
                    x = position.x + width;
                    y -= Constants.blockSize;
                    canvas.drawRect(x, y, Constants.blockSize * 3 + x, Constants.blockSize + y, paint);
                    x += Constants.blockSize*3;
                    y += Constants.blockSize;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);
                    break;
                case 4:
                    // left arm
                    x = position.x - Constants.blockSize * 3;
                    y = position.y + Constants.blockSize * 4;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);
                    x += Constants.blockSize;
                    y -= Constants.blockSize;
                    canvas.drawRect(x, y, Constants.blockSize * 2 + x, Constants.blockSize + y, paint);

                    // right arm
                    x = position.x + width;
                    y = position.y + h - Constants.blockSize * 4;
                    canvas.drawRect(x, y, Constants.blockSize * 2 + x, Constants.blockSize + y, paint);
                    x += Constants.blockSize * 2;
                    y -= Constants.blockSize;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);

                    // left leg
                    x = position.x - Constants.blockSize;
                    y = position.y + h - Constants.blockSize;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);
                    x -= Constants.blockSize;
                    y -= Constants.blockSize;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);
                    x -= Constants.blockSize;
                    y -= Constants.blockSize;
                    canvas.drawRect(x, y, Constants.blockSize * 2 + x, Constants.blockSize + y, paint);

                    // right leg
                    x = position.x + width;
                    y = position.y + h;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);
                    x += Constants.blockSize;
                    y += Constants.blockSize;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);
                    x += Constants.blockSize;
                    y += Constants.blockSize;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);
                    x += Constants.blockSize;
                    y += Constants.blockSize;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);
                    x += Constants.blockSize;
                    y -= Constants.blockSize;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);
                    break;
                case 5:
                    // left arm
                    x = position.x - Constants.blockSize * 2;
                    y = position.y + Constants.blockSize * 4;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize * 2 + y, paint);
                    x += Constants.blockSize;
                    y -= Constants.blockSize;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);

                    // right arm
                    x = position.x + width;
                    y = position.y + h - Constants.blockSize * 4;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);
                    x += Constants.blockSize;
                    y += Constants.blockSize;
                    canvas.drawRect(x, y, Constants.blockSize * 2 + x, Constants.blockSize + y, paint);

                    // left leg
                    x = position.x - Constants.blockSize * 2;
                    y = position.y + h;
                    canvas.drawRect(x, y, Constants.blockSize * 2 + x, Constants.blockSize + y, paint);
                    x -= Constants.blockSize;
                    y -= Constants.blockSize;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);
                    x -= Constants.blockSize;
                    y -= Constants.blockSize;
                    canvas.drawRect(x, y, Constants.blockSize * 2 + x, Constants.blockSize + y, paint);

                    // right leg
                    x = position.x + width - Constants.blockSize;
                    y = position.y + h;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);
                    x += Constants.blockSize;
                    y += Constants.blockSize;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);
                    x += Constants.blockSize;
                    y += Constants.blockSize;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);
                    y += Constants.blockSize;
                    canvas.drawRect(x, y, Constants.blockSize * 2 + x, Constants.blockSize + y, paint);
                    break;
                case 6:
                    // left arm
                    x = position.x - Constants.blockSize;
                    y = position.y + Constants.blockSize * 4;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize * 3 + y, paint);

                    // right arm
                    x = position.x + width;
                    y += Constants.blockSize * 4;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);
                    x += Constants.blockSize;
                    y += Constants.blockSize;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);

                    // left leg
                    x = position.x;
                    y = position.y + h;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);
                    x -= Constants.blockSize * 3;
                    y += Constants.blockSize;
                    canvas.drawRect(x, y, Constants.blockSize * 3 + x, Constants.blockSize + y, paint);
                    y += Constants.blockSize;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);

                    // right leg
                    x = position.x + width - Constants.blockSize * 2;
                    y = position.y + h;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize + y, paint);
                    x += Constants.blockSize;
                    y += Constants.blockSize;
                    canvas.drawRect(x, y, Constants.blockSize + x, Constants.blockSize * 2 + y, paint);
                    y += Constants.blockSize * 2;
                    canvas.drawRect(x, y, Constants.blockSize * 2 + x, Constants.blockSize + y, paint);
                    break;
            }

            // body
            canvas.drawRect(position.x, position.y, width + position.x, h + position.y, paint);

            // eye slot
            DrawEyeSlot(canvas);
        }
    }
}
