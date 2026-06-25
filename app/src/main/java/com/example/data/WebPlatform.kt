package com.example.data

data class WebPlatform(
    val id: String,
    val name: String,
    val category: String, // "AI Assistants", "Study & Research", "Coding & Compilers", "Math & STEM"
    val url: String,
    val searchUrlTemplate: String? = null,
    val description: String,
    val badgeColorHex: Long = 0xFF1E40AF
)

object PlatformsRepo {
    val allPlatforms = listOf(
        // AI Assistants
        WebPlatform("chatgpt", "ChatGPT", "AI Assistants", "https://chatgpt.com", "https://chatgpt.com/?q=", "OpenAI premier conversational assistant & code troubleshooter", 0xFF10A37F),
        WebPlatform("claude", "Claude AI", "AI Assistants", "https://claude.ai", null, "Anthropic advanced reasoning & long-form document analyzer", 0xFFD97757),
        WebPlatform("perplexity", "Perplexity AI", "AI Assistants", "https://www.perplexity.ai", "https://www.perplexity.ai/search?q=", "Real-time cited academic search engine & knowledge synthesizer", 0xFF22B8CD),
        WebPlatform("gemini", "Google Gemini", "AI Assistants", "https://gemini.google.com", null, "Google multimodal reasoning & live web search portal", 0xFF4285F4),
        WebPlatform("deepseek", "DeepSeek Coder", "AI Assistants", "https://chat.deepseek.com", null, "Open-weights algorithmic coding & mathematical specialist", 0xFF4A90E2),

        // Study & Research
        WebPlatform("scholar", "Google Scholar", "Study & Research", "https://scholar.google.com", "https://scholar.google.com/scholar?q=", "Search peer-reviewed papers, theses, books, and court opinions", 0xFF34A853),
        WebPlatform("khan", "Khan Academy", "Study & Research", "https://www.khanacademy.org", "https://www.khanacademy.org/search?page_search_query=", "Free world-class interactive courses for math, science & computing", 0xFF14BF96),
        WebPlatform("coursera", "Coursera", "Study & Research", "https://www.coursera.org", "https://www.coursera.org/search?query=", "University degrees, professional certificates & lecture notes", 0xFF0056D2),
        WebPlatform("arxiv", "arXiv Research", "Study & Research", "https://arxiv.org", "https://arxiv.org/search/?query=", "Cornell open-access e-prints in Physics, CS, Math & Quant Bio", 0xFFB31B1B),
        WebPlatform("wikipedia", "Wikipedia", "Study & Research", "https://en.wikipedia.org", "https://en.wikipedia.org/wiki/Special:Search?search=", "The free encyclopedia with 6M+ academic reference articles", 0xFF333333),

        // Math & STEM
        WebPlatform("wolfram", "WolframAlpha", "Math & STEM", "https://www.wolframalpha.com", "https://www.wolframalpha.com/input/?i=", "Computational intelligence engine for integrals, physics & chem", 0xFFFF7A00),
        WebPlatform("desmos", "Desmos Graphing", "Math & STEM", "https://www.desmos.com/calculator", null, "Advanced interactive graphing calculator & geometry tool", 0xFF2F80ED),

        // Coding & Compilers
        WebPlatform("leetcode", "LeetCode", "Coding & Compilers", "https://leetcode.com/problemset/", "https://leetcode.com/problemset/?search=", "Premier platform for technical interview prep & algorithms", 0xFFFFA116),
        WebPlatform("github", "GitHub", "Coding & Compilers", "https://github.com", "https://github.com/search?q=", "World's largest developer repository & code collaboration hub", 0xFF24292E),
        WebPlatform("replit", "Replit Cloud IDE", "Coding & Compilers", "https://replit.com", null, "Instant collaborative browser IDE supporting 50+ languages", 0xFFF26207),
        WebPlatform("hackerrank", "HackerRank", "Coding & Compilers", "https://www.hackerrank.com/dashboard", null, "Master data structures, SQL, Linux shell & AI challenges", 0xFF00EA64),
        WebPlatform("codeforces", "Codeforces", "Coding & Compilers", "https://codeforces.com/problemset", null, "Competitive programming contests & algorithmic training rounds", 0xFF1F8ACB),
        WebPlatform("stackoverflow", "Stack Overflow", "Coding & Compilers", "https://stackoverflow.com", "https://stackoverflow.com/search?q=", "Q&A community for software developers & bug debugging", 0xFFF48024),
        WebPlatform("kaggle", "Kaggle AI Labs", "Coding & Compilers", "https://www.kaggle.com", "https://www.kaggle.com/search?q=", "Machine learning datasets, GPU notebooks & AI competitions", 0xFF20BEFF),
        WebPlatform("colab", "Google Colab", "Coding & Compilers", "https://colab.research.google.com", null, "Jupyter notebooks executing Python code on free Google GPUs", 0xFFF9AB00)
    )
}
