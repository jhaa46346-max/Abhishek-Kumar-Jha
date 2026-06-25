package com.example.data

data class WebPlatform(
    val id: String,
    val name: String,
    val category: String, // "AI Assistants", "Study & Research", "Coding & Compilers", "Math & STEM", "Engineering & Degree"
    val url: String,
    val searchUrlTemplate: String? = null,
    val description: String,
    val badgeColorHex: Long = 0xFF1E40AF
)

object PlatformsRepo {
    val allPlatforms = listOf(
        // AI Assistants
        WebPlatform("chatgpt", "ChatGPT (GPT-4o & o1)", "AI Assistants", "https://chatgpt.com", "https://chatgpt.com/?q=", "OpenAI premier conversational assistant & reasoning solver", 0xFF10A37F),
        WebPlatform("claude", "Claude 3.5 Sonnet", "AI Assistants", "https://claude.ai", null, "Anthropic advanced coding, architecture & research analyzer", 0xFFD97757),
        WebPlatform("perplexity", "Perplexity Pro", "AI Assistants", "https://www.perplexity.ai", "https://www.perplexity.ai/search?q=", "Real-time cited academic search engine & literature synthesizer", 0xFF22B8CD),
        WebPlatform("gemini", "Google Gemini Advanced", "AI Assistants", "https://gemini.google.com", null, "Google multimodal reasoning & live student web search portal", 0xFF4285F4),
        WebPlatform("deepseek", "DeepSeek V3 / R1", "AI Assistants", "https://chat.deepseek.com", null, "Open-weights algorithmic mathematical reasoning specialist", 0xFF4A90E2),
        WebPlatform("huggingface", "HuggingFace Chat", "AI Assistants", "https://huggingface.co/chat", null, "Open-source Llama 3, Mistral & Qwen models arena", 0xFFFFD21E),
        WebPlatform("grok", "xAI Grok", "AI Assistants", "https://x.com/i/grok", null, "Real-time global news & engineering physics solver", 0xFF000000),
        WebPlatform("copilot", "Microsoft Copilot", "AI Assistants", "https://copilot.microsoft.com", null, "Enterprise web-grounded student research assistant", 0xFF0078D4),

        // Coding & Compilers
        WebPlatform("leetcode", "LeetCode Solutions", "Coding & Compilers", "https://leetcode.com/problemset/", "https://leetcode.com/problemset/?search=", "Premier platform for technical interview prep & algorithms", 0xFFFFA116),
        WebPlatform("github", "GitHub Hub", "Coding & Compilers", "https://github.com", "https://github.com/search?q=", "World's largest developer repository & code collaboration hub", 0xFF24292E),
        WebPlatform("replit", "Replit Agent IDE", "Coding & Compilers", "https://replit.com", null, "Instant collaborative browser IDE supporting 50+ languages", 0xFFF26207),
        WebPlatform("cursor", "Cursor Web Docs", "Coding & Compilers", "https://docs.cursor.com", null, "AI-first code editor guides & agentic coding patterns", 0xFF6366F1),
        WebPlatform("v0", "v0 by Vercel AI", "Coding & Compilers", "https://v0.dev", null, "Generative UI frontend component builder & React generator", 0xFF000000),
        WebPlatform("hackerrank", "HackerRank Practice", "Coding & Compilers", "https://www.hackerrank.com/dashboard", null, "Master data structures, SQL, Linux shell & AI challenges", 0xFF00EA64),
        WebPlatform("codeforces", "Codeforces Contests", "Coding & Compilers", "https://codeforces.com/problemset", null, "Competitive programming contests & algorithmic training rounds", 0xFF1F8ACB),
        WebPlatform("stackoverflow", "Stack Overflow Q&A", "Coding & Compilers", "https://stackoverflow.com", "https://stackoverflow.com/search?q=", "Q&A community for software developers & bug debugging", 0xFFF48024),
        WebPlatform("kaggle", "Kaggle AI Labs", "Coding & Compilers", "https://www.kaggle.com", "https://www.kaggle.com/search?q=", "Machine learning datasets, GPU notebooks & AI competitions", 0xFF20BEFF),
        WebPlatform("colab", "Google Colab Notebooks", "Coding & Compilers", "https://colab.research.google.com", null, "Jupyter notebooks executing Python code on free Google GPUs", 0xFFF9AB00),

        // Engineering & Bachelor's Degree Topics
        WebPlatform("mitocw", "MIT OpenCourseWare", "Engineering & Degree", "https://ocw.mit.edu", "https://ocw.mit.edu/search/?q=", "Complete bachelor course notes, video lectures & exam solutions", 0xFFA31F34),
        WebPlatform("nptel", "NPTEL IIT Courses", "Engineering & Degree", "https://nptel.ac.in", null, "Core bachelor degree engineering curriculum & solved assignments", 0xFF003366),
        WebPlatform("paulsnotes", "Paul's Online Math Notes", "Engineering & Degree", "https://tutorial.math.lamar.edu", null, "Calculus I-III & DiffEq complete step-by-step solved problems", 0xFF2B6CB0),
        WebPlatform("geeksforgeeks", "GeeksforGeeks CS", "Engineering & Degree", "https://www.geeksforgeeks.org", "https://www.geeksforgeeks.org/search/?q=", "Complete solutions for Data Structures, OS, DBMS & Networks", 0xFF2F855A),
        WebPlatform("libretexts", "LibreTexts STEM", "Engineering & Degree", "https://eng.libretexts.org", null, "Open-access engineering bachelor textbooks & solved mechanics", 0xFF3182CE),
        WebPlatform("engtoolbox", "Engineering ToolBox", "Engineering & Degree", "https://www.engineeringtoolbox.com", null, "Thermodynamics, fluid dynamics, HVAC calculators & design tables", 0xFFD69E2E),
        WebPlatform("ieeexplore", "IEEE Xplore Library", "Engineering & Degree", "https://ieeexplore.ieee.org", "https://ieeexplore.ieee.org/search/searchresult.jsp?newsearch=true&queryText=", "Premier bachelor research papers, standards & thesis references", 0xFF00629B),
        WebPlatform("circuitlab", "CircuitLab Simulator", "Engineering & Degree", "https://www.circuitlab.com", null, "Interactive schematic editor & nodal circuit voltage solver", 0xFFDD6B20),
        WebPlatform("hyperphysics", "HyperPhysics Concepts", "Engineering & Degree", "http://hyperphysics.phy-astr.gsu.edu/hbase/hframe.html", null, "Comprehensive bachelor physics mindmaps & solved equations", 0xFF805AD5),
        WebPlatform("symbolab", "Symbolab Step Solver", "Engineering & Degree", "https://www.symbolab.com", "https://www.symbolab.com/solver/step-by-step/", "Complete mathematical step-by-step integral & algebra solutions", 0xFFE53E3E),

        // Study & Research
        WebPlatform("scholar", "Google Scholar", "Study & Research", "https://scholar.google.com", "https://scholar.google.com/scholar?q=", "Search peer-reviewed papers, theses, books, and court opinions", 0xFF34A853),
        WebPlatform("khan", "Khan Academy", "Study & Research", "https://www.khanacademy.org", "https://www.khanacademy.org/search?page_search_query=", "Free world-class interactive courses for math, science & computing", 0xFF14BF96),
        WebPlatform("coursera", "Coursera Degrees", "Study & Research", "https://www.coursera.org", "https://www.coursera.org/search?query=", "University degrees, professional certificates & lecture notes", 0xFF0056D2),
        WebPlatform("arxiv", "arXiv E-Prints", "Study & Research", "https://arxiv.org", "https://arxiv.org/search/?query=", "Cornell open-access e-prints in Physics, CS, Math & Quant Bio", 0xFFB31B1B),
        WebPlatform("wikipedia", "Wikipedia Reference", "Study & Research", "https://en.wikipedia.org", "https://en.wikipedia.org/wiki/Special:Search?search=", "The free encyclopedia with 6M+ academic reference articles", 0xFF333333),

        // Math & STEM
        WebPlatform("wolfram", "WolframAlpha Engine", "Math & STEM", "https://www.wolframalpha.com", "https://www.wolframalpha.com/input/?i=", "Computational intelligence engine for integrals, physics & chem", 0xFFFF7A00),
        WebPlatform("desmos", "Desmos Graphing Suite", "Math & STEM", "https://www.desmos.com/calculator", null, "Advanced interactive graphing calculator & geometry tool", 0xFF2F80ED)
    )
}
