package br.com.gitmatch.gitmatch.dto.vaga;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GitHubStatsDTO {
    private long totalBytesLinguagens;
    private int totalFrameworks;
    private String githubUsername;
}