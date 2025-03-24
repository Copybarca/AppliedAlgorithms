package audioHandler;
import org.example.ser.A;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class AudioWaveform extends JPanel {
    private List<Integer> amplitudes;

    public AudioWaveform(List<Integer> amplitudes) {
        this.amplitudes = amplitudes;
    }
    public AudioWaveform() {
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int width = getWidth();
        int height = getHeight();
        int midY = height / 2;

        g.drawLine(0, midY, width, midY);

        for (int i = 0; i < amplitudes.size() - 1; i++) {
            int x1 = (int) ((double) i / amplitudes.size() * width);
            int y1 = midY - amplitudes.get(i) / 256; // Нормализация амплитуды
            int x2 = (int) ((double) (i + 1) / amplitudes.size() * width);
            int y2 = midY - amplitudes.get(i + 1) / 256;

            g.drawLine(x1, y1, x2, y2);
        }
    }
    public void setAmplitudeValuesFromFile(String path){
        File wavFile = path==null || path.isEmpty() ? new File("src/main/java/audioHandler/resourses/sample-3s.wav")
                                  :new File(path);
        List<Integer> amplitudes = new ArrayList<>();
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(wavFile);
            AudioFormat format = audioInputStream.getFormat();
            int requiredBufferSize = format.getFrameSize() * (int)format.getSampleRate();

            byte[] buffer = new byte[requiredBufferSize]; // Размер буфера
            int sampleRate = 4000;
            int samplePerFrameRate = (int) format.getSampleRate() / sampleRate;
            int totalSamples = 2000000; // Максимальное количество семплов
            for (int j = 0; j < totalSamples; j += samplePerFrameRate) {
                audioInputStream.read(buffer);
                // Обработка данных
                for (int i = 0; i < buffer.length; i += 2) {
                    if (j + 1 < buffer.length) {
                        int amplitude = (buffer[j] & 0xFF) | (buffer[j + 1] << 8);
                        amplitudes.add(amplitude);
                    }
                }
            }
            this.amplitudes = amplitudes;
        } catch (UnsupportedAudioFileException | IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void setAmplitudeValuesFromList(List<Integer> amplitudeParams){
        this.amplitudes = amplitudeParams;
    }
    public void printAmplitudeDiagram(){
        JFrame frame = new JFrame("Audio Waveform");
        AudioWaveform waveformPanel = new AudioWaveform(amplitudes);
        frame.add(waveformPanel);
        frame.setSize(800, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }



    public static void main(String[] args) {
        AudioWaveform a = new AudioWaveform();
        a.setAmplitudeValuesFromFile("src/main/java/audioHandler/resourses/newCodedFile.wav");
        a.printAmplitudeDiagram();
    }
}
