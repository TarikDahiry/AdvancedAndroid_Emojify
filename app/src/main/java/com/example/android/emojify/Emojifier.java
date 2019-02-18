package com.example.android.emojify;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

class Emojifier {

    private static final String LOG_TAG = Emojifier.class.getSimpleName();
    private static final double SMILING_THRESHOLD = .15;
    private static final double EYE_OPEN_THRESHOLD = .5;

    // Check whether the image has faces or not
    static void detectFaces(Context context, Bitmap image) {

        FaceDetector detector = new FaceDetector.Builder(context)
                .setTrackingEnabled(false)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();
        // Create Frame instance from the bitmap to supply to the detector
        Frame frame = new Frame.Builder().setBitmap(image).build();
        //Detect faces
        SparseArray<Face> faces = detector.detect(frame);
        //Check the number of faces in the image
        if (faces.size() > 0) {
            Log.d(LOG_TAG, "There are " + faces.size() + " in this image");
            for (int i = 0; i < faces.size(); ++i) {
                Face face = faces.valueAt(i);
                // Log the classification probabilities for each face.
                whichEmoji(face);
            }
        } else {
            Toast.makeText(context, "No faces detected in this image", Toast.LENGTH_LONG).show();
        }
        // Release the detector
        detector.release();
    }

    private static void whichEmoji(Face face) {
        // Log all the probabilities
        Log.d(LOG_TAG, "getClassifications: smilingProb = " + face.getIsSmilingProbability());
        Log.d(LOG_TAG, "getClassifications: leftEyeOpenProb = " + face.getIsLeftEyeOpenProbability());
        Log.d(LOG_TAG, "getClassifications: rightEyeOpenProb = " + face.getIsRightEyeOpenProbability());

        // Determine the appropriate emoji
        Emoji emoji;
        boolean smiling = face.getIsSmilingProbability() > SMILING_THRESHOLD;
        boolean leftEyeClosed = face.getIsLeftEyeOpenProbability() < EYE_OPEN_THRESHOLD;
        boolean rightEyeClosed = face.getIsRightEyeOpenProbability() < EYE_OPEN_THRESHOLD;
        if (smiling) {
            if (leftEyeClosed && !rightEyeClosed) {
                emoji = Emoji.LEFT_WINK;
            } else if (rightEyeClosed && !leftEyeClosed) {
                emoji = Emoji.RIGHT_WINK;
            } else if (leftEyeClosed) {
                emoji = Emoji.CLOSED_EYE_SMILE;
            } else {
                emoji = Emoji.SMILE;
            }
        } else {
            if (leftEyeClosed && !rightEyeClosed) {
                emoji = Emoji.LEFT_WINK_FROWN;
            } else if (!leftEyeClosed && rightEyeClosed) {
                emoji = Emoji.RIGHT_WINK_FROWN;
            } else if (leftEyeClosed) {
                emoji = Emoji.CLOSED_EYE_FROWN;
            } else {
                emoji = Emoji.FROWN;
            }
        }
        // Log the chosen Emoji
        Log.d(LOG_TAG, "whichEmoji: " + emoji.name());
    }

    // Enum for all possible Emojis
    private enum Emoji {
        SMILE,
        FROWN,
        LEFT_WINK,
        RIGHT_WINK,
        LEFT_WINK_FROWN,
        RIGHT_WINK_FROWN,
        CLOSED_EYE_SMILE,
        CLOSED_EYE_FROWN
    }
}

