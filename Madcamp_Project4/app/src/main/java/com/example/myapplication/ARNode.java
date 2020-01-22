package com.example.myapplication;


import android.content.Context;
import android.util.Log;

import com.google.ar.core.AugmentedImage;
import com.google.ar.core.Pose;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ARNode extends AnchorNode {
    private AugmentedImage image;
    private final String capsuleid;

    public static CompletableFuture <ModelRenderable> modelRenderableCompletableFuture;

    public void setNode() {
        //this.image = image;
        if (!modelRenderableCompletableFuture.isDone()) {
            CompletableFuture.allOf(modelRenderableCompletableFuture)
                    .thenAccept((Void aVoid) -> {
                        //setImage(image);
                        setNode();
                    }).exceptionally(throwable -> {
                        return null;
            });
        }

        //setAnchor(image.createAnchor(image.getCenterPose()));

        Node node = new Node();
        Pose pose = Pose.makeTranslation(0.0f, 0.0f, 0.0f);

        node.setParent(this);

        node.setLocalPosition(new Vector3(pose.tx(), pose.ty(), pose.tz()));
        node.setLocalRotation(new Quaternion(pose.qx(), pose.qy(), pose.qz(), pose.qw()));
        //node.setLocalScale(new Vector3(0.2f*pose.tx(), 0.2f*pose.ty(), 0.2f*pose.tz()));
        //node.setLocalScale(new Vector3(pose.tx(), pose.ty(), pose.tz()));
        node.setLocalScale(new Vector3(0.1f, 0.1f, 0.1f));
        node.setRenderable(modelRenderableCompletableFuture.getNow(null));

    }

    public ARNode (Context context, int modelId, String cid)
    {this.capsuleid =cid;
        if (modelRenderableCompletableFuture == null) {


            modelRenderableCompletableFuture = ModelRenderable.builder()
                    .setRegistryId("capsule")
                    .setSource(context, modelId)
                    .build();
//                    .thenAccept(modelRenderable -> onRenderableLoaded(modelRenderable))
//                    .exceptionally(throwable -> {
//                        Log.i("Node", "failed to load model");
//                        return null;
//                    });

//            CompletableFuture.allOf(modelRenderableCompletableFuture)
//                    .handle(
//                            (notUsed, throwable) ->
//                            {
//                                if (throwable != null) {
//                                    //DemoUtils.displayError(this, "Unable to load renderables", throwable);
//                                    Log.i("loading", "unable to load renderables");
//                                    return null;
//                                }
//
//                                try {
//                                    andyRenderable = modelRenderableCompletableFuture.get();
//
//                                } catch (InterruptedException | ExecutionException ex) {
//                                    //DemoUtils.displayError(this, "Unable to load renderables", ex);
//                                    Log.i("loading", "unable to load renderables");
//                                }
//                                return null;
//                            });
        }




    }

    private void onRenderableLoaded(ModelRenderable modelRenderable) {
        Node node = new Node();
        Pose pose = Pose.makeTranslation(0.0f, 0.0f, 0.25f);

        node.setParent(this);
        node.setLocalPosition(new Vector3(pose.tx(), pose.ty(), pose.tz()));
        node.setLocalRotation(new Quaternion(pose.qx(), pose.qy(), pose.qz(), pose.qw()));
        //node.setRenderable(modelRenderableCompletableFuture.getNow(null));
        node.setRenderable(modelRenderableCompletableFuture.getNow(null));

    }

    public AugmentedImage getImage() {
        return image;
    }
    public String getCapsuleid(){return capsuleid;}
}
