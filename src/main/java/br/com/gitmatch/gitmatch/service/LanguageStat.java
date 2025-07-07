package br.com.gitmatch.gitmatch.service;

class LanguageStat {
    private String language;
    private long bytes;
    private double percentage;

    public LanguageStat(String language, long bytes, double percentage) {
        this.language = language;
        this.bytes = bytes;
        this.percentage = percentage;
    }

    public String getLanguage() {
        return language;
    }

    public long getBytes() {
        return bytes;
    }

    public double getPercentage() {
        return percentage;
    }

    @Override
    public String toString() {
        return String.format("%s: %.2f%%", language, percentage);
    }
}