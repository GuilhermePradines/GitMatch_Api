package br.com.gitmatch.gitmatch.service;

import java.net.URI;
import java.net.http.*;
import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

public class GitHubLangStats {

    public static List<List<Object>> buscarLinguagensEFrameworksDetalhados(String githubUsername) throws Exception {
    String token = "";

    HttpClient client = HttpClient.newHttpClient();
    ObjectMapper mapper = new ObjectMapper();

    HttpRequest requestRepos = HttpRequest.newBuilder()
        .uri(URI.create("https://api.github.com/users/" + githubUsername + "/repos"))
        .header("Accept", "application/vnd.github.v3+json")
        .header("Authorization", "token " + token)
        .build();

    HttpResponse<String> responseRepos = client.send(requestRepos, HttpResponse.BodyHandlers.ofString());
    if (responseRepos.statusCode() != 200) {
        throw new RuntimeException("Erro ao buscar repositórios: " + responseRepos.body());
    }

    List<Map<String, Object>> repos = mapper.readValue(responseRepos.body(), new TypeReference<>() {});
    Map<String, Long> linguagens = new HashMap<>();
    Map<String, Integer> frameworks = new HashMap<>();

    for (Map<String, Object> repo : repos) {
        String languagesUrl = (String) repo.get("languages_url");
        String contentsUrl = (String) repo.get("url") + "/contents";

        // Linguagens
        HttpRequest requestLang = HttpRequest.newBuilder()
            .uri(URI.create(languagesUrl))
            .header("Accept", "application/vnd.github.v3+json")
            .header("Authorization", "token " + token)
            .build();

        HttpResponse<String> responseLang = client.send(requestLang, HttpResponse.BodyHandlers.ofString());
        if (responseLang.statusCode() == 200) {
            Map<String, Long> langs = mapper.readValue(responseLang.body(), new TypeReference<>() {});
            for (Map.Entry<String, Long> entry : langs.entrySet()) {
                linguagens.merge(entry.getKey(), entry.getValue(), Long::sum);
            }
        }

        // Frameworks
        Set<String> frameworksDetectados = new HashSet<>();

        HttpRequest requestContents = HttpRequest.newBuilder()
            .uri(URI.create(contentsUrl))
            .header("Accept", "application/vnd.github.v3+json")
            .header("Authorization", "token " + token)
            .build();

        HttpResponse<String> responseContents = client.send(requestContents, HttpResponse.BodyHandlers.ofString());
        if (responseContents.statusCode() == 200) {
            List<Map<String, Object>> contents = mapper.readValue(responseContents.body(), new TypeReference<>() {});

            for (Map<String, Object> item : contents) {
                String name = ((String) item.get("name")).toLowerCase();

                if (name.equals("package.json")) {
                    String fileUrl = (String) item.get("download_url");
                    HttpRequest fileRequest = HttpRequest.newBuilder().uri(URI.create(fileUrl)).build();
                    HttpResponse<String> fileResponse = client.send(fileRequest, HttpResponse.BodyHandlers.ofString());
                    if (fileResponse.statusCode() == 200) {
                        String content = fileResponse.body().toLowerCase();
                        if (content.contains("\"react\"")) frameworksDetectados.add("React");
                        if (content.contains("\"express\"")) frameworksDetectados.add("Express");
                        if (content.contains("\"next\"")) frameworksDetectados.add("Next.js");
                        if (content.contains("\"expo\"")) frameworksDetectados.add("Expo");
                        if (content.contains("\"vue\"")) frameworksDetectados.add("Vue.js");
                        if (content.contains("\"@angular")) frameworksDetectados.add("Angular");
                    }
                }

                if (name.equals("angular.json")) frameworksDetectados.add("Angular");
                if (name.equals("vue.config.js")) frameworksDetectados.add("Vue.js");
                if (name.equals("pom.xml") || name.equals("build.gradle")) frameworksDetectados.add("Spring Boot");
                if (name.equals("manage.py") || name.equals("settings.py")) frameworksDetectados.add("Django");

                if (name.equals("requirements.txt")) {
                    String fileUrl = (String) item.get("download_url");
                    HttpRequest fileRequest = HttpRequest.newBuilder().uri(URI.create(fileUrl)).build();
                    HttpResponse<String> fileResponse = client.send(fileRequest, HttpResponse.BodyHandlers.ofString());
                    if (fileResponse.statusCode() == 200) {
                        String content = fileResponse.body().toLowerCase();
                        if (content.contains("flask")) frameworksDetectados.add("Flask");
                        if (content.contains("django")) frameworksDetectados.add("Django");
                        if (content.contains("fastapi")) frameworksDetectados.add("FastAPI");
                    }
                }

                if (name.equals("composer.json")) {
                    String fileUrl = (String) item.get("download_url");
                    HttpRequest fileRequest = HttpRequest.newBuilder().uri(URI.create(fileUrl)).build();
                    HttpResponse<String> fileResponse = client.send(fileRequest, HttpResponse.BodyHandlers.ofString());
                    if (fileResponse.statusCode() == 200) {
                        String content = fileResponse.body().toLowerCase();
                        if (content.contains("laravel")) frameworksDetectados.add("Laravel");
                    }
                }

                if (name.endsWith(".csproj") || name.endsWith(".sln") || name.equals("program.cs")) {
                    frameworksDetectados.add("ASP.NET");
                }

                if (name.equals("app.json")) frameworksDetectados.add("Expo");
            }
        }

        for (String fw : frameworksDetectados) {
            frameworks.merge(fw, 1, Integer::sum);
        }
    }

    List<List<Object>> resultado = new ArrayList<>();

    linguagens.forEach((nome, bytes) -> resultado.add(List.of(nome, bytes)));
    frameworks.forEach((nome, qtdProjetos) -> resultado.add(List.of(nome, qtdProjetos)));

    return resultado;
}




    public static List<String> buscarLinguagensPorUsername(String githubUsername) throws Exception {
        String token = ""; // token opcional

        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper();

        HttpRequest requestRepos = HttpRequest.newBuilder()
            .uri(URI.create("https://api.github.com/users/" + githubUsername + "/repos"))
            .header("Accept", "application/vnd.github.v3+json")
            .header("Authorization", "token " + token)
            .build();

        HttpResponse<String> responseRepos = client.send(requestRepos, HttpResponse.BodyHandlers.ofString());

        if (responseRepos.statusCode() != 200) {
            throw new RuntimeException("Erro ao buscar repositórios: " + responseRepos.body());
        }

        List<Map<String, Object>> repos = mapper.readValue(responseRepos.body(), new TypeReference<>() {});
        Set<String> tecnologias = new HashSet<>();

        for (Map<String, Object> repo : repos) {
            String languagesUrl = (String) repo.get("languages_url");
            String contentsUrl = ((String) repo.get("url")) + "/contents";

            // Linguagens
            HttpRequest requestLang = HttpRequest.newBuilder()
                .uri(URI.create(languagesUrl))
                .header("Accept", "application/vnd.github.v3+json")
                .header("Authorization", "token " + token)
                .build();

            HttpResponse<String> responseLang = client.send(requestLang, HttpResponse.BodyHandlers.ofString());
            if (responseLang.statusCode() == 200) {
                Map<String, Long> langs = mapper.readValue(responseLang.body(), new TypeReference<>() {});
                tecnologias.addAll(langs.keySet()); // adiciona linguagens direto
            }

            // Frameworks e tecnologias por nome de arquivo
            HttpRequest requestContents = HttpRequest.newBuilder()
                .uri(URI.create(contentsUrl))
                .header("Accept", "application/vnd.github.v3+json")
                .header("Authorization", "token " + token)
                .build();

            HttpResponse<String> responseContents = client.send(requestContents, HttpResponse.BodyHandlers.ofString());

            if (responseContents.statusCode() == 200) {
                List<Map<String, Object>> contents = mapper.readValue(responseContents.body(), new TypeReference<>() {});

                for (Map<String, Object> item : contents) {
                    String name = ((String) item.get("name")).toLowerCase();

                    // JavaScript
                    if (name.equals("package.json")) {
                        String fileUrl = (String) item.get("download_url");
                        HttpRequest fileRequest = HttpRequest.newBuilder().uri(URI.create(fileUrl)).build();
                        HttpResponse<String> fileResponse = client.send(fileRequest, HttpResponse.BodyHandlers.ofString());
                        if (fileResponse.statusCode() == 200) {
                            String content = fileResponse.body().toLowerCase();
                            if (content.contains("\"react\"")) tecnologias.add("React");
                            if (content.contains("\"express\"")) tecnologias.add("Express");
                            if (content.contains("\"next\"")) tecnologias.add("Next.js");
                            if (content.contains("\"expo\"")) tecnologias.add("Expo");
                            if (content.contains("\"vue\"")) tecnologias.add("Vue.js");
                            if (content.contains("\"@angular")) tecnologias.add("Angular");
                        }
                    }
                    if (name.equals("angular.json")) tecnologias.add("Angular");
                    if (name.equals("vue.config.js")) tecnologias.add("Vue.js");

                    // Java
                    if (name.equals("pom.xml") || name.equals("build.gradle")) tecnologias.add("Spring Boot");

                    // Python
                    if (name.equals("manage.py") || name.equals("settings.py")) tecnologias.add("Django");
                    if (name.equals("requirements.txt")) {
                        String fileUrl = (String) item.get("download_url");
                        HttpRequest fileRequest = HttpRequest.newBuilder().uri(URI.create(fileUrl)).build();
                        HttpResponse<String> fileResponse = client.send(fileRequest, HttpResponse.BodyHandlers.ofString());
                        if (fileResponse.statusCode() == 200) {
                            String content = fileResponse.body().toLowerCase();
                            if (content.contains("flask")) tecnologias.add("Flask");
                            if (content.contains("django")) tecnologias.add("Django");
                            if (content.contains("fastapi")) tecnologias.add("FastAPI");
                        }
                    }

                    // PHP
                    if (name.equals("composer.json")) {
                        String fileUrl = (String) item.get("download_url");
                        HttpRequest fileRequest = HttpRequest.newBuilder().uri(URI.create(fileUrl)).build();
                        HttpResponse<String> fileResponse = client.send(fileRequest, HttpResponse.BodyHandlers.ofString());
                        if (fileResponse.statusCode() == 200) {
                            String content = fileResponse.body().toLowerCase();
                            if (content.contains("laravel")) tecnologias.add("Laravel");
                        }
                    }

                    // .NET
                    if (name.endsWith(".csproj") || name.endsWith(".sln") || name.equals("program.cs")) {
                        tecnologias.add("ASP.NET");
                    }

                    // Mobile
                    if (name.equals("app.json")) tecnologias.add("Expo");
                }
            }
        }

        return new ArrayList<>(tecnologias);
    }


public static List<String> buscarTecnologiasComPorcentagem(String githubUsername) throws Exception {
    String token = "";

    HttpClient client = HttpClient.newHttpClient();
    ObjectMapper mapper = new ObjectMapper();

    HttpRequest requestRepos = HttpRequest.newBuilder()
        .uri(URI.create("https://api.github.com/users/" + githubUsername + "/repos"))
        .header("Accept", "application/vnd.github.v3+json")
        .header("Authorization", "token " + token)
        .build();

    HttpResponse<String> responseRepos = client.send(requestRepos, HttpResponse.BodyHandlers.ofString());
    if (responseRepos.statusCode() != 200) {
        throw new RuntimeException("Erro ao buscar repositórios: " + responseRepos.body());
    }

    List<Map<String, Object>> repos = mapper.readValue(responseRepos.body(), new TypeReference<>() {});
    Map<String, Long> linguagensBytes = new HashMap<>();
    Map<String, Integer> frameworksContagem = new HashMap<>();

    for (Map<String, Object> repo : repos) {
        String languagesUrl = (String) repo.get("languages_url");
        String contentsUrl = (String) repo.get("url") + "/contents";

        // LINGUAGENS - soma bytes
        HttpRequest requestLang = HttpRequest.newBuilder()
            .uri(URI.create(languagesUrl))
            .header("Accept", "application/vnd.github.v3+json")
            .header("Authorization", "token " + token)
            .build();

        HttpResponse<String> responseLang = client.send(requestLang, HttpResponse.BodyHandlers.ofString());
        if (responseLang.statusCode() == 200) {
            Map<String, Long> langs = mapper.readValue(responseLang.body(), new TypeReference<>() {});
            for (Map.Entry<String, Long> entry : langs.entrySet()) {
                linguagensBytes.merge(entry.getKey(), entry.getValue(), Long::sum);
            }
        }

        // FRAMEWORKS - contador por repositório (set para evitar duplicação no mesmo repo)
        Set<String> frameworksDetectadosNoRepo = new HashSet<>();

        HttpRequest requestContents = HttpRequest.newBuilder()
            .uri(URI.create(contentsUrl))
            .header("Accept", "application/vnd.github.v3+json")
            .header("Authorization", "token " + token)
            .build();

        HttpResponse<String> responseContents = client.send(requestContents, HttpResponse.BodyHandlers.ofString());

        if (responseContents.statusCode() == 200) {
            List<Map<String, Object>> contents = mapper.readValue(responseContents.body(), new TypeReference<>() {});

            for (Map<String, Object> item : contents) {
                String name = ((String) item.get("name")).toLowerCase();

                if (name.equals("package.json")) {
                    String fileUrl = (String) item.get("download_url");
                    HttpRequest fileRequest = HttpRequest.newBuilder().uri(URI.create(fileUrl)).build();
                    HttpResponse<String> fileResponse = client.send(fileRequest, HttpResponse.BodyHandlers.ofString());
                    if (fileResponse.statusCode() == 200) {
                        String content = fileResponse.body().toLowerCase();
                        if (content.contains("\"react\"")) frameworksDetectadosNoRepo.add("React");
                        if (content.contains("\"express\"")) frameworksDetectadosNoRepo.add("Express");
                        if (content.contains("\"next\"")) frameworksDetectadosNoRepo.add("Next.js");
                        if (content.contains("\"expo\"")) frameworksDetectadosNoRepo.add("Expo");
                        if (content.contains("\"vue\"")) frameworksDetectadosNoRepo.add("Vue.js");
                        if (content.contains("\"@angular")) frameworksDetectadosNoRepo.add("Angular");
                    }
                }

                if (name.equals("angular.json")) frameworksDetectadosNoRepo.add("Angular");
                if (name.equals("vue.config.js")) frameworksDetectadosNoRepo.add("Vue.js");

                if (name.equals("pom.xml") || name.equals("build.gradle")) frameworksDetectadosNoRepo.add("Spring Boot");

                if (name.equals("manage.py") || name.equals("settings.py")) frameworksDetectadosNoRepo.add("Django");

                if (name.equals("requirements.txt")) {
                    String fileUrl = (String) item.get("download_url");
                    HttpRequest fileRequest = HttpRequest.newBuilder().uri(URI.create(fileUrl)).build();
                    HttpResponse<String> fileResponse = client.send(fileRequest, HttpResponse.BodyHandlers.ofString());
                    if (fileResponse.statusCode() == 200) {
                        String content = fileResponse.body().toLowerCase();
                        if (content.contains("flask")) frameworksDetectadosNoRepo.add("Flask");
                        if (content.contains("django")) frameworksDetectadosNoRepo.add("Django");
                        if (content.contains("fastapi")) frameworksDetectadosNoRepo.add("FastAPI");
                    }
                }

                if (name.equals("composer.json")) {
                    String fileUrl = (String) item.get("download_url");
                    HttpRequest fileRequest = HttpRequest.newBuilder().uri(URI.create(fileUrl)).build();
                    HttpResponse<String> fileResponse = client.send(fileRequest, HttpResponse.BodyHandlers.ofString());
                    if (fileResponse.statusCode() == 200) {
                        String content = fileResponse.body().toLowerCase();
                        if (content.contains("laravel")) frameworksDetectadosNoRepo.add("Laravel");
                    }
                }

                if (name.endsWith(".csproj") || name.endsWith(".sln") || name.equals("program.cs")) {
                    frameworksDetectadosNoRepo.add("ASP.NET");
                }

                if (name.equals("app.json")) frameworksDetectadosNoRepo.add("Expo");
            }
        }

        // Adiciona ao contador global
        for (String framework : frameworksDetectadosNoRepo) {
            frameworksContagem.merge(framework, 1, Integer::sum);
        }
    }

    // Calcula porcentagem para linguagens
    long totalBytes = linguagensBytes.values().stream().mapToLong(Long::longValue).sum();
    List<String> resultado = new ArrayList<>();

    for (Map.Entry<String, Long> entry : linguagensBytes.entrySet()) {
        String linguagem = entry.getKey();
        long bytes = entry.getValue();
        double porcentagem = (bytes * 100.0) / totalBytes;
        resultado.add(linguagem + ": " + String.format("%.1f", porcentagem) + "%");
    }

    // Adiciona frameworks com contagem de projetos
    for (Map.Entry<String, Integer> entry : frameworksContagem.entrySet()) {
        String framework = entry.getKey();
        int count = entry.getValue();
        String projetoStr = count == 1 ? "projeto" : "projetos";
        resultado.add(framework + " (" + count + " " + projetoStr + ")");
    }

    return resultado;
}

}
