package de.zigldrum.ihnn.objects;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import de.zigldrum.ihnn.BuildConfig;

public class AppState implements Serializable {

    private static final long serialVersionUID = 2242379700997586835L;
    private static final String appStateFile = "AppState.db";
    public final static String BASE_URL = BuildConfig.DEBUG ? "http://spackenserver.de:11337/" : "http://spackenserver.de:11337/";

    private boolean initialized;


    private List<ContentPack> packs;
    private List<Question> questions;

    private boolean enableNSFW;
    private boolean enableAutoUpdates;
    private boolean onlyNSFW;

    private Set<Integer> disabledPacks;

    public AppState() {
        initialized = false;
        packs = new ArrayList<>();
        questions = new ArrayList<>();
        this.enableNSFW = false;
        this.enableAutoUpdates = true;
        this.onlyNSFW = false;
        this.disabledPacks = new HashSet<>();
    }

    public void setEnableNSFW(boolean enable) {
        this.enableNSFW = enable;
    }

    public void setEnableAutoUpdates(boolean enable) {
        this.enableAutoUpdates = enable;
    }

    public boolean getEnableNSFW() {
        return enableNSFW;
    }

    public boolean getEnableAutoUpdates(){
        return enableAutoUpdates;
    }

    public boolean isOnlyNSFW() {
        return onlyNSFW;
    }

    public void setOnlyNSFW(boolean onlyNSFW) {
        this.onlyNSFW = onlyNSFW;
    }

    public Set<Integer> getDisabledPacks() {
        if(disabledPacks == null) disabledPacks = new HashSet<>();
        return disabledPacks;
    }

    public void setDisabledPacks(Set<Integer> disabledPacks) {
        this.disabledPacks = disabledPacks;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public List<ContentPack> getPacks() {
        return packs;
    }

    public void setPacks(List<ContentPack> packs) {
        this.packs = packs;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public boolean saveState (File baseDir) {
        try (ObjectOutputStream stateOut = new ObjectOutputStream(new FileOutputStream(new File(baseDir, appStateFile)))){
            stateOut.writeObject(this);
            System.out.println("Successfully stored AppState to: " + baseDir.getAbsolutePath());
            System.out.println("Other files: " + Arrays.stream(baseDir.listFiles()).map(f -> f.getAbsolutePath()).collect(Collectors.toList()).toString());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error when writing AppState!");
            return false;
        }
    }

    public static AppState loadState(File baseDir) {
        AppState state = null;
        try (ObjectInputStream stateIn = new ObjectInputStream(new FileInputStream(new File(baseDir, appStateFile)))) {
            System.out.println("Attempting to parse AppState.");
            state = (AppState) stateIn.readObject();
            System.out.println("Successfully read AppState.");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        } finally {
            return state;
        }
    }

    public static boolean isFirstStart (File baseDir) {
        System.out.println("Checking if AppState File exists");
        System.out.println("Files: " + Arrays.stream(baseDir.listFiles()).map(f -> f.getAbsolutePath()).collect(Collectors.toList()).toString());
        Optional<File> appFile = Arrays.stream(baseDir.listFiles()).filter(f -> f.getAbsolutePath().endsWith(appStateFile)).findFirst();
        if(appFile.isPresent()) {
            System.out.println("Found file: " + appFile.get());
            System.out.print("Permissions: ");
            if (appFile.get().canRead()) System.out.print("r");
            if (appFile.get().canWrite()) System.out.print("w");
            if (appFile.get().canExecute()) System.out.print("x");
            System.out.print("\n");

            return false;
        } else {
            return true;
        }
    }
}
