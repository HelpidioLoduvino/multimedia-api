package com.example.multimediaapi.service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class VideoCompressionService {

    public File compressVideo(File source, String outputFilePath) throws IOException, InterruptedException {
        File target = new File(outputFilePath);

        // Certifique-se de que o diretório de destino exista
        File targetDir = target.getParentFile();
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }

        // Comando do ffmpeg para compressão
        String[] command = {
                "ffmpeg",
                "-i", source.getAbsolutePath(),     // Arquivo de entrada
                "-vcodec", "libx264",               // Codec de vídeo
                "-crf", "35",                       // Taxa de compressão (quanto maior o valor, maior a compressão e menor a qualidade)
                "-preset", "fast",                  // Preset de velocidade (ajusta a complexidade da compressão, "fast" é uma boa escolha para balancear qualidade e tempo)
                "-vf", "scale=640:-1",              // Redimensiona o vídeo para largura de 640px mantendo a proporção
                "-acodec", "aac",                   // Codec de áudio
                "-b:a", "128k",                     // Taxa de bits de áudio
                //"-b:v", "800k",                     // Taxa de bits de vídeo
                target.getAbsolutePath()            // Arquivo de saída
        };

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();

        // Log output
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("Erro na compressão do vídeo, código de saída: " + exitCode);
        }

        return target;
    }

}
