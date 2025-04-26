package ru.nstu.lab02v2.Add;


import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;

public class Serializer<T>{
    private String path;
    private String fileName;
    private File file;

    public Serializer(String path, String fileName){
        this.path = path;
        this.fileName = fileName;
        this.file = new File(path + fileName);
    }
    public Serializer(String fileName){
        this("src/main/resources/ru/nstu/lab02v2/AppFiles", fileName);
    }
    public Serializer(){
        this("src/main/resources/ru/nstu/lab02v2/AppFiles", "save.json");
    }

    public void serialize(final T object){
        ObjectMapper mapper = new ObjectMapper(); // Создаем экземпляр ObjectMapper

        try {
            mapper.writeValue(file, object); // Сериализуем объект в JSON файл
            System.out.println("Данные симуляции сохранены в " + path + fileName);

        } catch (IOException e) {
            System.err.println("Ошибка при сохранении данных симуляции: " + e.getMessage());
        }
    }

    public T deserialize(Class thisClass) {
        ObjectMapper mapper = new ObjectMapper();
        T object = null;

        try {
            object = (T) mapper.readValue(file, thisClass); // Десериализуем из JSON файла
            System.out.println("Данные симуляции загружены из " + path + fileName);
        } catch (IOException e) {
            System.err.println("Ошибка при загрузке данных симуляции: " + e.getMessage());
            // Если файл не найден или ошибка, возвращаем null или новый экземпляр SimulationData
            return null; // Или null, в зависимости от логики приложения
        }

        return object;
    }



    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public void setPath(String path) {
        this.path = path;
    }
}
