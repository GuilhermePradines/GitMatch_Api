package br.com.gitmatch.gitmatch.service;

import java.net.URI;
import java.net.http.*;
import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import br.com.gitmatch.gitmatch.service.LanguageStat;
import java.util.List;
import java.util.ArrayList;

public class GitHubLangStats {

    public static List<String> buscarLinguagensPorUsername(String githubUsername) throws Exception {
        String token = ""; // ou sem token, mas sujeito a rate limit

        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper();

        HttpRequest requestRepos = HttpRequest.newBuilder()
            .uri(URI.create("https://api.github.com/users/" + githubUsername + "/repos"))
            .header("Accept", "application/vnd.github.v3+json")
            .header("Authorization", "token " + token) // Remova essa linha se for usar sem token
            .build();

        HttpResponse<String> responseRepos = client.send(requestRepos, HttpResponse.BodyHandlers.ofString());

        if (responseRepos.statusCode() != 200) {
            throw new RuntimeException("Erro ao buscar repositórios: " + responseRepos.body());
        }

        List<Map<String, Object>> repos = mapper.readValue(responseRepos.body(), new TypeReference<>() {});
        Set<String> linguagens = new HashSet<>();

        for (Map<String, Object> repo : repos) {
            String languagesUrl = (String) repo.get("languages_url");

            HttpRequest requestLang = HttpRequest.newBuilder()
                .uri(URI.create(languagesUrl))
                .header("Accept", "application/vnd.github.v3+json")
                .header("Authorization", "token " + token)
                .build();

            HttpResponse<String> responseLang = client.send(requestLang, HttpResponse.BodyHandlers.ofString());

            if (responseLang.statusCode() != 200) {
                continue; // ignora esse repo
            }

            Map<String, Long> langs = mapper.readValue(responseLang.body(), new TypeReference<>() {});
            linguagens.addAll(langs.keySet());
        }

        return new ArrayList<>(linguagens); // retorna como lista simples
    }
}
