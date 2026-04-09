package il.cshaifasweng.OCSFMediatorExample.client.util.assets;

import animatefx.animation.FadeIn;
import animatefx.animation.FadeInUp;
import animatefx.animation.FadeOut;
import animatefx.animation.Shake;
import javafx.animation.TranslateTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.ColorAdjust;
import javafx.util.Duration;

public class Animations {

    public static void fadeInUp(Node node) {
        new FadeInUp(node).play();
    }

    public static void fadeOut(Node node) {
        new FadeOut(node).play();
    }

    public static void shake(Node node) {
        new Shake(node).play();
    }

    public static void fadeOutWithDuration(Node node) {
        FadeOut fadeOut = new FadeOut(node);
        fadeOut.setSpeed(10);
        fadeOut.play();
    }

    public static void fade(Node parent, Node node, Node icon) {
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(0.0);

        node.setEffect(colorAdjust);

        parent.setOnMouseEntered(e -> {
            icon.setVisible(true);
            fadeInUp(icon);

            Timeline fadeInTimeline = new Timeline(
                    new KeyFrame(Duration.seconds(0),
                            new KeyValue(colorAdjust.brightnessProperty(), colorAdjust.brightnessProperty().getValue(), Interpolator.LINEAR)),
                    new KeyFrame(Duration.seconds(0.2), new KeyValue(colorAdjust.brightnessProperty(), -0.6, Interpolator.LINEAR)
                    ));
            fadeInTimeline.setCycleCount(1);
            fadeInTimeline.setAutoReverse(false);
            fadeInTimeline.play();
        });

        parent.setOnMouseExited(e -> {
            fadeOut(icon);

            Timeline fadeOutTimeline = new Timeline(
                    new KeyFrame(Duration.seconds(0),
                            new KeyValue(colorAdjust.brightnessProperty(), colorAdjust.brightnessProperty().getValue(), Interpolator.LINEAR)),
                    new KeyFrame(Duration.seconds(0.2), new KeyValue(colorAdjust.brightnessProperty(), 0, Interpolator.LINEAR)
                    ));
            fadeOutTimeline.setCycleCount(1);
            fadeOutTimeline.setAutoReverse(false);
            fadeOutTimeline.play();
        });
    }

    public static void tooltip(Node node, Node tooltip) {
        node.setOnMouseEntered(ev -> {
            FadeIn fadeIn = new FadeIn(tooltip);
            fadeIn.setSpeed(3);
            fadeIn.play();
            tooltip.setVisible(true);
        });

        node.setOnMouseExited(ev -> tooltip.setVisible(false));
    }

    public static void hover(Node node, int duration, double setXAndY) {
        ScaleTransition scaleTrans = new ScaleTransition(Duration.millis(duration), node);
        scaleTrans.setFromX(1.0);
        scaleTrans.setFromY(1.0);
        scaleTrans.setToX(setXAndY);
        scaleTrans.setToY(setXAndY);

        node.setOnMouseEntered(ev -> {
            scaleTrans.setRate(1.0);
            scaleTrans.play();
        });

        node.setOnMouseExited(ev -> {
            scaleTrans.setRate(-1.0);
            scaleTrans.play();
        });
    }

    public static void progressAnimation(ProgressBar progressBar, double value) {
        Timeline timeline = new Timeline();

        KeyValue keyValue = new KeyValue(progressBar.progressProperty(), value);
        KeyFrame keyFrame = new KeyFrame(new Duration(600), keyValue);
        timeline.getKeyFrames().add(keyFrame);

        timeline.play();
    }

    public static void slideOut(Node node, Duration duration, boolean toRight) {
        TranslateTransition transition = new TranslateTransition(duration, node);
        transition.setFromX(0);
        transition.setToX(toRight ? 50 : -50); // תנועה ימינה או שמאלה
        transition.setInterpolator(Interpolator.EASE_BOTH);
        transition.setOnFinished(event -> node.setTranslateX(0)); // חזרה למיקום המקורי
        transition.play();
    }

    public static void slideIn(Node node, Duration duration, boolean fromRight) {
        TranslateTransition transition = new TranslateTransition(duration, node);
        transition.setFromX(fromRight ? -50 : 50); // תנועה מימין או משמאל
        transition.setToX(0);
        transition.setInterpolator(Interpolator.EASE_BOTH);
        transition.setOnFinished(event -> node.setTranslateX(0)); // חזרה למיקום המקורי
        transition.play();
    }

    public static void slide(Node outNode, Node inNode, Duration duration, boolean toRight) {
        TranslateTransition outTransition = new TranslateTransition(duration, outNode);
        outTransition.setFromX(0);
        outTransition.setToX(toRight ? 200 : -200); // תנועה ימינה או שמאלה
        outTransition.setInterpolator(Interpolator.EASE_BOTH);
        outTransition.setOnFinished(event -> outNode.setTranslateX(0)); // חזרה למיקום המקורי

        TranslateTransition inTransition = new TranslateTransition(duration, inNode);
        inTransition.setFromX(toRight ? -200 : 200); // תנועה מימין או משמאל
        inTransition.setToX(0);
        inTransition.setInterpolator(Interpolator.EASE_BOTH);
        inTransition.setOnFinished(event -> inNode.setTranslateX(0)); // חזרה למיקום המקורי

        outTransition.play();
        inTransition.play();
    }
}
