package me.dueris.genesismc.files;

import org.bukkit.Server;

import java.io.*;
import java.util.Properties;

public class ServerProperties {
    private Properties properties;
    private File config;

    public ServerProperties(File config){
        this.config = config;
        properties = new Properties();
        loadProperties();
    }

    public void loadProperties(){
        try (FileInputStream inputStream = new FileInputStream(config)){
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean containsProperty(String key){
        return properties.containsKey(key);
    }

    public String getProperty(String key){
        return properties.getProperty(key);
    }

    @Override
    public String toString() {
        return "ServerProperties{" +
                "properties=" + properties +
                ", config=" + config +
                '}';
    }

    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }

    public void deleteProperty(String key) {
        properties.remove(key);
    }

    public void resetProperties() {
        try (InputStream defaultPropertiesStream = getClass().getResourceAsStream("/default-server.properties");
             OutputStream output = new FileOutputStream(config)) {

            properties.clear();
            properties.load(defaultPropertiesStream);
            properties.store(output, null);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveProperties() {
        try (FileOutputStream output = new FileOutputStream(config)) {
            properties.store(output, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
