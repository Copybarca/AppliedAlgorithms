package audioHandler;

import lombok.Getter;

import javax.sound.sampled.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class WavAnalyzer {
    @Getter
    List<Integer> ampList;
    String strAmplitude;
    static int HEADER_SIZE = 44;
    @Getter
    byte[] originalAudioData;
    private static byte[] intToByteArray(int value) {
        return new byte[] {
                (byte) ((value >> 24) & 0xFF),
                (byte) ((value >> 16) & 0xFF),
                (byte) ((value >> 8) & 0xFF),
                (byte) (value & 0xFF)
        };
    }
    private static byte[] shortToByteArrayLittleEnding(short value) {
        return new byte[] {
                (byte) (value & 0xFF),
                (byte) ((value >> 8) & 0xFF)
        };
    }
    private static byte[] intToByteArrayLittleEnding(int value) {
        return new byte[] {
                (byte) (value & 0xFF),
                (byte) ((value >> 8) & 0xFF),
                (byte) ((value >> 16) & 0xFF),
                (byte) ((value >> 24) & 0xFF)
        };
    }
    private static byte[] charToByteArray(char value) {
        return new byte[] {
                (byte) ((value >> 8) & 0xFF),
                (byte) (value & 0xFF)
        };
    }
    private String getNameFromPath(String path) throws IllegalArgumentException {
        if(path.isEmpty())
            throw new IllegalArgumentException();
        String [] pathArr = path.split("/");
        String fileName = pathArr[pathArr.length-1];
        pathArr = fileName.split("\\.");
        fileName = pathArr[0];
        return fileName;
    }
    public void getAmplitudesListFromFile(String path, int skipSampPerPass) throws FileNotFoundException {
        ampList = new ArrayList<>();
        if(path.isEmpty())
            throw new FileNotFoundException();
        String fileName = getNameFromPath(path);
        byte[] buffer = new byte[0];
        int requiredBufferSize;

        try
        {
            File audioFile = new File(path);//это нарушает солид и другие ньюшные операторы тоже
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            AudioFormat format = audioStream.getFormat();

            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);

            requiredBufferSize = format.getFrameSize() * (int)audioStream.getFrameLength();
            buffer = new byte[requiredBufferSize];

            line.open(format);
            line.start();
            audioStream.read(buffer);
            line.stop();
            line.close();
            audioStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        StringBuilder result = new StringBuilder();
        char prev = 65535;
        skipSampPerPass = skipSampPerPass % 2 == 0 ? skipSampPerPass : skipSampPerPass + 1;
        for (int i = 0; i < buffer.length; i += 2) {
            int amplitude = (buffer[i] & 0xFF) | (buffer[i + 1] << 8);//little-endian формат поэтому наоборот извлекаем
            ampList.add(amplitude);
            char symbol = (char)amplitude;
            //if(prev != symbol){
                result.append(symbol);//будут добавляться только амплитуды которые отличны от предыдущей
                prev = symbol;
            //}
            i += skipSampPerPass;
        }

        strAmplitude = result.toString();//нарушает солид
        // Вывод результата
        System.out.println(result);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName+" decodedTiString.txt"))) {//нарушает солид
            writer.write(result.toString());
            writer.newLine(); // Добавляет новую строку, если нужно
            System.out.println("Строка успешно записана в файл: " + fileName+" decodedTiString.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        originalAudioData = buffer;//нарушает солид

    }

    public static void writeWavFile(String filePath, byte[] audioData) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(new File(filePath))) {
            // Запись заголовка
            fos.write("RIFF".getBytes()); // Часть 1: Заголовок RIFF
            fos.write(intToByteArrayLittleEnding(HEADER_SIZE + audioData.length - 8)); // Размер файла
            fos.write("WAVE".getBytes()); // Часть 2: Заголовок WAVE
            fos.write("fmt ".getBytes()); // Часть 3: Заголовок fmt
            fos.write(intToByteArrayLittleEnding(16)); // Размер структуры fmt
            fos.write(shortToByteArrayLittleEnding((short) 1)); // Тип формата (PCM)
            fos.write(shortToByteArrayLittleEnding((short) 2)); // Количество каналов (1 - моно)
            fos.write(intToByteArrayLittleEnding(44100)); // Частота дискретизации
            fos.write(intToByteArrayLittleEnding(44100)); // Битрейт
            fos.write(shortToByteArrayLittleEnding((short) 4)); // Размер выборки
            fos.write(shortToByteArrayLittleEnding((short) 16)); // Битность (16 бит)
            fos.write("data".getBytes()); // Часть 4: Заголовок data
            fos.write(intToByteArrayLittleEnding(audioData.length)); // Размер данных
            // Запись аудиоданных
            fos.write(audioData);
        }
    }
    public byte[] stringToByteArray(String text){
        byte[]codedArray = new byte[text.length()*2];
        for (int i = 0,j=0; i < codedArray.length; i+=2,j++) {
            codedArray[i+1] = charToByteArray(text.charAt(j))[0];//младший стал старшим из-за Little-ending кодировки формата wav
            codedArray[i] = charToByteArray(text.charAt(j))[1];
        }
        return codedArray;
    }


    public static void main(String[] args) {

        WavAnalyzer f = new WavAnalyzer();
        try {
            f.getAmplitudesListFromFile("src/main/java/audioHandler/resourses/newCodedFile.wav",0);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        byte[] codedWord = f.stringToByteArray("ОО аа п");
        byte[] buffer = f.getOriginalAudioData();
        for (int i = 0; i < codedWord.length; i+=1) {
            buffer[i] = codedWord[i];
        }

        try {
            writeWavFile("src/main/java/audioHandler/resourses/newCodedFile.wav",buffer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        AudioWaveform graph = new AudioWaveform();
        graph.setAmplitudeValuesFromList(f.getAmpList());
        graph.printAmplitudeDiagram();

    }

}
