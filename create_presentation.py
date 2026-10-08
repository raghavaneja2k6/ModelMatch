import os
import sys
from pptx import Presentation
from pptx.util import Inches, Pt
from pptx.dml.color import RGBColor
from pptx.enum.text import PP_ALIGN, MSO_ANCHOR
from pptx.enum.shapes import MSO_SHAPE

# ==============================================================================
# COLOR PALETTE (ChatGPT / Modern AI Startup Aesthetic)
# Pure Black / Near Black canvas with crisp white typography & subtle accents
# ==============================================================================
COLOR_BG = RGBColor(10, 10, 10)           # #0A0A0A Pure Dark Canvas
COLOR_CARD_BG = RGBColor(20, 20, 20)      # #141414 Elevated Card
COLOR_CARD_BG_ALT = RGBColor(26, 26, 26)  # #1A1A1A Slightly brighter card
COLOR_BORDER = RGBColor(40, 40, 40)       # #282828 Subtle gray border
COLOR_BORDER_LIGHT = RGBColor(60, 60, 60) # #3C3C3C
COLOR_TEXT_WHITE = RGBColor(255, 255, 255)# #FFFFFF Primary text
COLOR_TEXT_LIGHT = RGBColor(220, 220, 220)# #DCDCDC High-contrast body
COLOR_TEXT_MUTED = RGBColor(150, 150, 150)# #969696 Secondary / captions
COLOR_TEXT_DIM = RGBColor(110, 110, 110)  # #6E6E6E
COLOR_ACCENT = RGBColor(16, 163, 127)     # #10A37F OpenAI Emerald Accent
COLOR_ACCENT_BG = RGBColor(14, 40, 32)    # #0E2820 Subtle emerald tint
COLOR_ACCENT_BORDER = RGBColor(28, 85, 62)# #1C553E
COLOR_ACCENT_BLUE = RGBColor(59, 130, 246)# #3B82F6 Blue accent for specific nodes
COLOR_CARD_HIGHLIGHT = RGBColor(28, 32, 30)

FONT_HEADING = "Segoe UI"
FONT_BODY = "Segoe UI"
FONT_CODE = "Consolas"

# ==============================================================================
# HELPER FUNCTIONS
# ==============================================================================
def create_deck():
    prs = Presentation()
    prs.slide_width = Inches(13.333)
    prs.slide_height = Inches(7.5)
    return prs

def set_slide_background(slide, color=COLOR_BG):
    background = slide.background
    fill = background.fill
    fill.solid()
    fill.fore_color.rgb = color

def add_header(slide, title_text, category_text, slide_number_str):
    # Top Category Pill
    pill = slide.shapes.add_shape(
        MSO_SHAPE.ROUNDED_RECTANGLE,
        Inches(0.8), Inches(0.45), Inches(3.2), Inches(0.3)
    )
    pill.fill.solid()
    pill.fill.fore_color.rgb = COLOR_ACCENT_BG
    pill.line.color.rgb = COLOR_ACCENT_BORDER
    pill.line.width = Pt(1)
    tf_pill = pill.text_frame
    tf_pill.word_wrap = False
    tf_pill.vertical_anchor = MSO_ANCHOR.MIDDLE
    tf_pill.margin_left = Inches(0.12)
    tf_pill.margin_top = Inches(0)
    p_pill = tf_pill.paragraphs[0]
    p_pill.text = category_text.upper()
    p_pill.font.name = FONT_HEADING
    p_pill.font.size = Pt(9.5)
    p_pill.font.bold = True
    p_pill.font.color.rgb = COLOR_ACCENT

    # Slide Number on Right
    num_box = slide.shapes.add_textbox(Inches(11.2), Inches(0.45), Inches(1.33), Inches(0.3))
    tf_num = num_box.text_frame
    tf_num.word_wrap = False
    tf_num.vertical_anchor = MSO_ANCHOR.MIDDLE
    p_num = tf_num.paragraphs[0]
    p_num.alignment = PP_ALIGN.RIGHT
    p_num.text = slide_number_str
    p_num.font.name = FONT_BODY
    p_num.font.size = Pt(11)
    p_num.font.bold = True
    p_num.font.color.rgb = COLOR_TEXT_MUTED

    # Main Title
    title_box = slide.shapes.add_textbox(Inches(0.8), Inches(0.78), Inches(11.73), Inches(0.65))
    tf_title = title_box.text_frame
    tf_title.word_wrap = True
    tf_title.margin_left = Inches(0)
    tf_title.margin_top = Inches(0)
    p_title = tf_title.paragraphs[0]
    p_title.text = title_text
    p_title.font.name = FONT_HEADING
    p_title.font.size = Pt(22)
    p_title.font.bold = True
    p_title.font.color.rgb = COLOR_TEXT_WHITE

def add_footer(slide):
    # Divider line
    line = slide.shapes.add_shape(
        MSO_SHAPE.RECTANGLE,
        Inches(0.8), Inches(6.9), Inches(11.73), Inches(0.015)
    )
    line.fill.solid()
    line.fill.fore_color.rgb = COLOR_BORDER
    line.line.color.rgb = COLOR_BORDER
    line.line.width = Pt(0)

    # Footer Left
    box_l = slide.shapes.add_textbox(Inches(0.8), Inches(6.95), Inches(6.0), Inches(0.35))
    tf_l = box_l.text_frame
    tf_l.word_wrap = False
    tf_l.margin_left = Inches(0)
    p_l = tf_l.paragraphs[0]
    p_l.text = "MODELMATCH · AI-POWERED LLM RECOMMENDATION ENGINE"
    p_l.font.name = FONT_BODY
    p_l.font.size = Pt(8.5)
    p_l.font.color.rgb = COLOR_TEXT_DIM

    # Footer Right
    box_r = slide.shapes.add_textbox(Inches(7.0), Inches(6.95), Inches(5.53), Inches(0.35))
    tf_r = box_r.text_frame
    tf_r.word_wrap = False
    p_r = tf_r.paragraphs[0]
    p_r.alignment = PP_ALIGN.RIGHT
    p_r.text = "GALGOTIAS UNIVERSITY / GUVI PROJECT BOARD REVIEW 1"
    p_r.font.name = FONT_BODY
    p_r.font.size = Pt(8.5)
    p_r.font.color.rgb = COLOR_TEXT_DIM

def create_card_shape(slide, left, top, width, height, bg_color=COLOR_CARD_BG, border_color=COLOR_BORDER, border_pt=1):
    card = slide.shapes.add_shape(
        MSO_SHAPE.ROUNDED_RECTANGLE,
        left, top, width, height
    )
    card.fill.solid()
    card.fill.fore_color.rgb = bg_color
    card.line.color.rgb = border_color
    card.line.width = Pt(border_pt)
    return card

# ==============================================================================
# SLIDE BUILDERS (10 SLIDES EXACTLY)
# ==============================================================================

def build_slide_1(prs):
    """SLIDE 1 — TITLE"""
    slide = prs.slides.add_slide(prs.slide_layouts[6])
    set_slide_background(slide)

    # Top Tag
    pill = slide.shapes.add_shape(
        MSO_SHAPE.ROUNDED_RECTANGLE,
        Inches(4.66), Inches(0.75), Inches(4.0), Inches(0.38)
    )
    pill.fill.solid()
    pill.fill.fore_color.rgb = COLOR_ACCENT_BG
    pill.line.color.rgb = COLOR_ACCENT_BORDER
    pill.line.width = Pt(1.2)
    tf = pill.text_frame
    tf.vertical_anchor = MSO_ANCHOR.MIDDLE
    p = tf.paragraphs[0]
    p.alignment = PP_ALIGN.CENTER
    p.text = "PROJECT BOARD REVIEW 1 — JAVA WEB APPLICATION"
    p.font.name = FONT_HEADING
    p.font.size = Pt(10)
    p.font.bold = True
    p.font.color.rgb = COLOR_ACCENT

    # Logo Image
    logo_path = os.path.join("slides_assets", "logo.png")
    if os.path.exists(logo_path):
        slide.shapes.add_picture(logo_path, Inches(6.166), Inches(1.35), Inches(1.0), Inches(1.0))

    # Main Title
    title_box = slide.shapes.add_textbox(Inches(1.5), Inches(2.45), Inches(10.33), Inches(0.8))
    tf_t = title_box.text_frame
    tf_t.word_wrap = True
    p_t = tf_t.paragraphs[0]
    p_t.alignment = PP_ALIGN.CENTER
    p_t.text = "MODEL MATCH"
    p_t.font.name = FONT_HEADING
    p_t.font.size = Pt(36)
    p_t.font.bold = True
    p_t.font.color.rgb = COLOR_TEXT_WHITE

    # Subtitle
    sub_box = slide.shapes.add_textbox(Inches(1.5), Inches(3.25), Inches(10.33), Inches(0.5))
    tf_s = sub_box.text_frame
    p_s = tf_s.paragraphs[0]
    p_s.alignment = PP_ALIGN.CENTER
    p_s.text = "AI-Powered Large Language Model Recommendation & Ranking Engine"
    p_s.font.name = FONT_BODY
    p_s.font.size = Pt(15)
    p_s.font.color.rgb = COLOR_TEXT_LIGHT

    # Tagline Card
    quote_card = create_card_shape(slide, Inches(2.5), Inches(3.9), Inches(8.33), Inches(0.9), bg_color=COLOR_CARD_BG, border_color=COLOR_BORDER_LIGHT)
    tf_q = quote_card.text_frame
    tf_q.vertical_anchor = MSO_ANCHOR.MIDDLE
    tf_q.margin_left = Inches(0.4)
    tf_q.margin_right = Inches(0.4)
    p_q = tf_q.paragraphs[0]
    p_q.alignment = PP_ALIGN.CENTER
    p_q.text = "“Tell us what you want to do, and ModelMatch tells you which AI model is best suited for it—and why.”"
    p_q.font.name = FONT_BODY
    p_q.font.size = Pt(13)
    p_q.font.italic = True
    p_q.font.color.rgb = COLOR_TEXT_WHITE

    # 3 Meta Information Cards at Bottom
    card_w = Inches(3.64)
    card_h = Inches(1.4)
    card_y = Inches(5.1)

    # Meta 1: Domain
    create_card_shape(slide, Inches(0.8), card_y, card_w, card_h)
    m1_box = slide.shapes.add_textbox(Inches(0.95), card_y + Inches(0.12), card_w - Inches(0.3), card_h - Inches(0.24))
    tf_m1 = m1_box.text_frame
    p_m1_h = tf_m1.paragraphs[0]
    p_m1_h.text = "PROJECT DOMAIN"
    p_m1_h.font.name = FONT_HEADING
    p_m1_h.font.size = Pt(9.5)
    p_m1_h.font.bold = True
    p_m1_h.font.color.rgb = COLOR_ACCENT
    p_m1_b1 = tf_m1.add_paragraph()
    p_m1_b1.text = "• AI Decision-Support System"
    p_m1_b1.font.name = FONT_BODY
    p_m1_b1.font.size = Pt(11)
    p_m1_b1.font.color.rgb = COLOR_TEXT_LIGHT
    p_m1_b2 = tf_m1.add_paragraph()
    p_m1_b2.text = "• Natural Language Task Analysis"
    p_m1_b2.font.name = FONT_BODY
    p_m1_b2.font.size = Pt(11)
    p_m1_b2.font.color.rgb = COLOR_TEXT_LIGHT
    p_m1_b3 = tf_m1.add_paragraph()
    p_m1_b3.text = "• Vector Similarity & Multi-Criteria Ranking"
    p_m1_b3.font.name = FONT_BODY
    p_m1_b3.font.size = Pt(11)
    p_m1_b3.font.color.rgb = COLOR_TEXT_LIGHT

    # Meta 2: Architecture
    create_card_shape(slide, Inches(4.84), card_y, card_w, card_h)
    m2_box = slide.shapes.add_textbox(Inches(4.99), card_y + Inches(0.12), card_w - Inches(0.3), card_h - Inches(0.24))
    tf_m2 = m2_box.text_frame
    p_m2_h = tf_m2.paragraphs[0]
    p_m2_h.text = "TECHNOLOGY STACK"
    p_m2_h.font.name = FONT_HEADING
    p_m2_h.font.size = Pt(9.5)
    p_m2_h.font.bold = True
    p_m2_h.font.color.rgb = COLOR_ACCENT
    p_m2_b1 = tf_m2.add_paragraph()
    p_m2_b1.text = "• Core Java 21 LTS & Virtual Threads"
    p_m2_b1.font.name = FONT_BODY
    p_m2_b1.font.size = Pt(11)
    p_m2_b1.font.color.rgb = COLOR_TEXT_LIGHT
    p_m2_b2 = tf_m2.add_paragraph()
    p_m2_b2.text = "• Google Gemini 3.5 Flash NLP API"
    p_m2_b2.font.name = FONT_BODY
    p_m2_b2.font.size = Pt(11)
    p_m2_b2.font.color.rgb = COLOR_TEXT_LIGHT
    p_m2_b3 = tf_m2.add_paragraph()
    p_m2_b3.text = "• Embedded HTTPServer · ChatGPT UI"
    p_m2_b3.font.name = FONT_BODY
    p_m2_b3.font.size = Pt(11)
    p_m2_b3.font.color.rgb = COLOR_TEXT_LIGHT

    # Meta 3: Evaluation Board
    create_card_shape(slide, Inches(8.88), card_y, card_w, card_h)
    m3_box = slide.shapes.add_textbox(Inches(9.03), card_y + Inches(0.12), card_w - Inches(0.3), card_h - Inches(0.24))
    tf_m3 = m3_box.text_frame
    p_m3_h = tf_m3.paragraphs[0]
    p_m3_h.text = "ACADEMIC BOARD"
    p_m3_h.font.name = FONT_HEADING
    p_m3_h.font.size = Pt(9.5)
    p_m3_h.font.bold = True
    p_m3_h.font.color.rgb = COLOR_ACCENT
    p_m3_b1 = tf_m3.add_paragraph()
    p_m3_b1.text = "• Galgotias University"
    p_m3_b1.font.name = FONT_BODY
    p_m3_b1.font.size = Pt(11)
    p_m3_b1.font.color.rgb = COLOR_TEXT_LIGHT
    p_m3_b2 = tf_m3.add_paragraph()
    p_m3_b2.text = "• GUVI Project Board Review 1"
    p_m3_b2.font.name = FONT_BODY
    p_m3_b2.font.size = Pt(11)
    p_m3_b2.font.color.rgb = COLOR_TEXT_LIGHT
    p_m3_b3 = tf_m3.add_paragraph()
    p_m3_b3.text = "• Track: Java Web Development & AI"
    p_m3_b3.font.name = FONT_BODY
    p_m3_b3.font.size = Pt(11)
    p_m3_b3.font.color.rgb = COLOR_TEXT_LIGHT

    add_footer(slide)

def build_slide_2(prs):
    """SLIDE 2 — PROBLEM STATEMENT"""
    slide = prs.slides.add_slide(prs.slide_layouts[6])
    set_slide_background(slide)
    add_header(slide, "The Problem: Navigating the LLM Frontier", "PROBLEM STATEMENT", "02 / 10")

    # Left Column: The Real-World Dilemma
    create_card_shape(slide, Inches(0.8), Inches(1.5), Inches(5.6), Inches(4.3))
    left_box = slide.shapes.add_textbox(Inches(1.05), Inches(1.65), Inches(5.1), Inches(4.0))
    tf_l = left_box.text_frame
    tf_l.word_wrap = True

    p1 = tf_l.paragraphs[0]
    p1.text = "The Frontier AI Selection Crisis"
    p1.font.name = FONT_HEADING
    p1.font.size = Pt(14)
    p1.font.bold = True
    p1.font.color.rgb = COLOR_TEXT_WHITE

    items = [
        ("Explosion of Models: ", "Dozens of frontier models (Claude 3.7 Sonnet, GPT-4o, Gemini 3.5 Flash, DeepSeek R1, Qwen 2.5) with radically different architectures and strengths."),
        ("Multi-Dimensional Trade-offs: ", "No single model dominates all axes. Superior reasoning often means slower latency; large context windows can inflate dollar costs."),
        ("Extreme Cost Disparity: ", "Token pricing varies by over 200× — from $0.075/1M tokens (Gemini Flash) up to $15.00/1M tokens (Claude Sonnet extended output)."),
        ("Lack of Explainability: ", "Engineers resort to subjective brand hype rather than empirical task matching, resulting in over-budget API bills or degraded task quality.")
    ]
    for title, desc in items:
        p_item = tf_l.add_paragraph()
        p_item.space_before = Pt(8)
        run_title = p_item.add_run()
        run_title.text = "• " + title
        run_title.font.name = FONT_BODY
        run_title.font.size = Pt(11)
        run_title.font.bold = True
        run_title.font.color.rgb = COLOR_TEXT_WHITE

        run_desc = p_item.add_run()
        run_desc.text = desc
        run_desc.font.name = FONT_BODY
        run_desc.font.size = Pt(10.5)
        run_desc.font.color.rgb = COLOR_TEXT_LIGHT

    # Right Column: Visual Flow of Decision Failure
    create_card_shape(slide, Inches(6.6), Inches(1.5), Inches(5.93), Inches(4.3))
    right_box = slide.shapes.add_textbox(Inches(6.85), Inches(1.65), Inches(5.4), Inches(4.0))
    tf_r = right_box.text_frame
    tf_r.word_wrap = True

    p_rh = tf_r.paragraphs[0]
    p_rh.text = "How Decisions Are Made Today vs. Reality"
    p_rh.font.name = FONT_HEADING
    p_rh.font.size = Pt(14)
    p_rh.font.bold = True
    p_rh.font.color.rgb = COLOR_TEXT_WHITE

    steps = [
        ("USER HAS A TASK", "e.g., Code a distributed Java microservice or summarize 100 PDF docs", COLOR_CARD_BG_ALT),
        ("CONFUSING MODEL LANDSCAPE", "12+ leading LLMs with conflicting marketing claims & benchmarks", COLOR_CARD_BG_ALT),
        ("MULTI-CRITERIA TENSION", "Reasoning vs. Coding vs. Context vs. Vision vs. Speed vs. Cost", COLOR_CARD_BG_ALT),
        ("SUBOPTIMAL SELECTION", "Defaulting to hyped models causes 5x-10x budget waste or context overflow", COLOR_ACCENT_BG)
    ]
    y_step = 2.2
    for i, (title, subtitle, bg) in enumerate(steps):
        s_card = create_card_shape(slide, Inches(6.9), Inches(y_step), Inches(5.33), Inches(0.68), bg_color=bg, border_color=COLOR_BORDER_LIGHT)
        tf_s = s_card.text_frame
        tf_s.vertical_anchor = MSO_ANCHOR.MIDDLE
        tf_s.margin_left = Inches(0.18)
        p_st = tf_s.paragraphs[0]
        p_st.text = f"STEP {i+1}: {title}"
        p_st.font.name = FONT_HEADING
        p_st.font.size = Pt(9.5)
        p_st.font.bold = True
        p_st.font.color.rgb = COLOR_ACCENT if bg == COLOR_ACCENT_BG else COLOR_TEXT_WHITE
        p_sd = tf_s.add_paragraph()
        p_sd.text = subtitle
        p_sd.font.name = FONT_BODY
        p_sd.font.size = Pt(9.5)
        p_sd.font.color.rgb = COLOR_TEXT_LIGHT
        y_step += 0.82

    # Bottom Punchline Banner
    banner = create_card_shape(slide, Inches(0.8), Inches(6.0), Inches(11.73), Inches(0.65), bg_color=COLOR_CARD_BG, border_color=COLOR_ACCENT_BORDER)
    tf_b = banner.text_frame
    tf_b.vertical_anchor = MSO_ANCHOR.MIDDLE
    p_b = tf_b.paragraphs[0]
    p_b.alignment = PP_ALIGN.CENTER
    r_b1 = p_b.add_run()
    r_b1.text = "CORE THESIS: "
    r_b1.font.bold = True
    r_b1.font.size = Pt(12)
    r_b1.font.color.rgb = COLOR_ACCENT
    r_b2 = p_b.add_run()
    r_b2.text = "“Choosing an AI model should be based on mathematical task suitability—not brand popularity.”"
    r_b2.font.bold = True
    r_b2.font.size = Pt(12)
    r_b2.font.color.rgb = COLOR_TEXT_WHITE

    add_footer(slide)

def build_slide_3(prs):
    """SLIDE 3 — PROPOSED SOLUTION"""
    slide = prs.slides.add_slide(prs.slide_layouts[6])
    set_slide_background(slide)
    add_header(slide, "Our Solution — ModelMatch Architecture", "PROPOSED SOLUTION", "03 / 10")

    # Pipeline cards across 4 columns (2 rows)
    col_w = Inches(2.78)
    gap_x = Inches(0.2)
    start_x = Inches(0.8)
    row_h = Inches(2.05)
    row1_y = Inches(1.55)
    row2_y = Inches(3.8)

    pipeline_cards = [
        # Row 1
        ("01. Natural Language Task", "Developer / User Input", "User freely describes their goal in plain English (e.g. 'Build a high-performance REST API with distributed caching on a strict budget').", COLOR_ACCENT),
        ("02. NLP Task Classification", "TfIdfClassifier & Corpus", "Java TF-IDF vectorizer + Gemini 3.5 Flash maps text into 8 categories (Coding, Reasoning, Writing, Multimodal, etc.).", COLOR_TEXT_WHITE),
        ("03. Requirement Extraction", "6D Vector Representation", "Extracts normalized requirements vector U = [R, C, X, M, S, P] in [0, 1]^6 reflecting task demands.", COLOR_TEXT_WHITE),
        ("04. Capability Database", "Empirical LLM Profiles", "12 frontier LLMs evaluated on verified benchmarks (SWE-bench Verified, MATH-500, MMLU-Pro, Context & Cost).", COLOR_TEXT_WHITE),
        # Row 2
        ("05. Vector Cosine Similarity", "Directional Geometry", "Calculates mathematical cosine angle Sim(U, M) = (U · M) / (||U|| ||M||) to find capability alignment.", COLOR_TEXT_WHITE),
        ("06. Priority Weighted Utility", "User-Tuned Weights", "Calculates weighted multi-criteria utility Score = Σ (w_i * S_i) matching explicit user slider preferences.", COLOR_TEXT_WHITE),
        ("07. Constraint Multiplier", "Hard Penalty Guardrails", "Applies severe penalties for boundary violations (e.g. text-only models penalized if vision is strictly required).", COLOR_TEXT_WHITE),
        ("08. Explainable AI Output", "Ranked Winner + Why", "Delivers final score %, strength highlights, potential drawbacks, cost estimate, and ranked alternatives.", COLOR_ACCENT),
    ]

    for idx, (title, tag, desc, title_col) in enumerate(pipeline_cards):
        col_idx = idx % 4
        row_y = row1_y if idx < 4 else row2_y
        x = start_x + col_idx * (col_w + gap_x)

        card = create_card_shape(slide, x, row_y, col_w, row_h, bg_color=COLOR_CARD_BG, border_color=COLOR_BORDER_LIGHT if title_col != COLOR_ACCENT else COLOR_ACCENT_BORDER)
        tf_c = card.text_frame
        tf_c.margin_left = Inches(0.18)
        tf_c.margin_right = Inches(0.18)
        tf_c.margin_top = Inches(0.15)

        p_t = tf_c.paragraphs[0]
        p_t.text = title
        p_t.font.name = FONT_HEADING
        p_t.font.size = Pt(10)
        p_t.font.bold = True
        p_t.font.color.rgb = title_col

        p_tag = tf_c.add_paragraph()
        p_tag.text = tag
        p_tag.font.name = FONT_BODY
        p_tag.font.size = Pt(8.5)
        p_tag.font.bold = True
        p_tag.font.color.rgb = COLOR_TEXT_MUTED
        p_tag.space_after = Pt(4)

        p_d = tf_c.add_paragraph()
        p_d.text = desc
        p_d.font.name = FONT_BODY
        p_d.font.size = Pt(9.5)
        p_d.font.color.rgb = COLOR_TEXT_LIGHT

    # Bottom Highlight Bar
    bar = create_card_shape(slide, Inches(0.8), Inches(6.05), Inches(11.73), Inches(0.6), bg_color=COLOR_ACCENT_BG, border_color=COLOR_ACCENT_BORDER)
    tf_bar = bar.text_frame
    tf_bar.vertical_anchor = MSO_ANCHOR.MIDDLE
    p_bar = tf_bar.paragraphs[0]
    p_bar.alignment = PP_ALIGN.CENTER
    r_bar = p_bar.add_run()
    r_bar.text = "KEY ADVANTAGE: Personalized, empirical decision-support instead of static hardcoded heuristics or trial-and-error."
    r_bar.font.name = FONT_HEADING
    r_bar.font.size = Pt(11.5)
    r_bar.font.bold = True
    r_bar.font.color.rgb = COLOR_ACCENT

    add_footer(slide)

def build_slide_4(prs):
    """SLIDE 4 — SYSTEM ARCHITECTURE"""
    slide = prs.slides.add_slide(prs.slide_layouts[6])
    set_slide_background(slide)
    add_header(slide, "System Architecture: Decoupled 5-Tier Design", "SYSTEM ARCHITECTURE", "04 / 10")

    # 5 Tiers arranged horizontally across the slide
    tier_w = Inches(2.18)
    tier_h = Inches(4.5)
    tier_gap = Inches(0.2)
    start_x = Inches(0.8)
    tier_y = Inches(1.5)

    tiers = [
        ("TIER 1", "Presentation Layer", "Client Web Browser",
         ["ChatGPT Replica UI", "Vanilla HTML5 / Modern CSS", "Vanilla ES6 JavaScript", "Zero Layout Shift", "Preset Sliders & Modals", "Token Cost Calculator"],
         COLOR_TEXT_WHITE),

        ("TIER 2", "Web & Server Layer", "Java 21 Concurrency",
         ["com.modelmatch.server", "Embedded HttpServer (8080)", "Virtual Threads Executor", "Executors.newVirtual...", "ApiHandler.java (REST API)", "Google Gson Serializer"],
         COLOR_TEXT_WHITE),

        ("TIER 3", "NLP & Intelligence", "Text Analysis Engine",
         ["TfIdfClassifier.java", "TaskCorpus Centroids", "8 Task Categories", "GeminiService.java", "Google Gemini 3.5 Flash", "Native java.net.http"],
         COLOR_ACCENT),

        ("TIER 4", "Recommendation Engine", "Vector Math & Scoring",
         ["RecommendationEngine", "TaskRequirementExtractor", "Vector Cosine Similarity", "Multi-Criteria Utility (w_i)", "Constraint Multipliers", "ExplainabilityService"],
         COLOR_ACCENT),

        ("TIER 5", "Data & Persistence", "Relational & Repository",
         ["ModelDatabase.java", "12 Frontier LLMs", "Empirical Benchmarks", "Relational Schema / DAO", "PreparedStatement Pool", "Query / Telemetry Logs"],
         COLOR_TEXT_WHITE)
    ]

    for idx, (tier_tag, tier_title, tier_sub, bullet_points, highlight_col) in enumerate(tiers):
        x = start_x + idx * (tier_w + tier_gap)
        card = create_card_shape(slide, x, tier_y, tier_w, tier_h, bg_color=COLOR_CARD_BG, border_color=COLOR_BORDER_LIGHT if highlight_col != COLOR_ACCENT else COLOR_ACCENT_BORDER)
        tf_t = card.text_frame
        tf_t.margin_left = Inches(0.14)
        tf_t.margin_right = Inches(0.14)
        tf_t.margin_top = Inches(0.15)

        p_tag = tf_t.paragraphs[0]
        p_tag.text = tier_tag
        p_tag.font.name = FONT_HEADING
        p_tag.font.size = Pt(9)
        p_tag.font.bold = True
        p_tag.font.color.rgb = highlight_col

        p_h = tf_t.add_paragraph()
        p_h.text = tier_title
        p_h.font.name = FONT_HEADING
        p_h.font.size = Pt(11)
        p_h.font.bold = True
        p_h.font.color.rgb = COLOR_TEXT_WHITE

        p_s = tf_t.add_paragraph()
        p_s.text = tier_sub
        p_s.font.name = FONT_BODY
        p_s.font.size = Pt(8.5)
        p_s.font.color.rgb = COLOR_TEXT_MUTED
        p_s.space_after = Pt(10)

        for pt in bullet_points:
            p_pt = tf_t.add_paragraph()
            p_pt.text = "• " + pt
            p_pt.font.name = FONT_BODY
            p_pt.font.size = Pt(9.5)
            p_pt.font.color.rgb = COLOR_TEXT_LIGHT
            p_pt.space_after = Pt(4)

    # Bottom Data Flow Bar
    flow_bar = create_card_shape(slide, Inches(0.8), Inches(6.15), Inches(11.73), Inches(0.55), bg_color=COLOR_CARD_BG_ALT, border_color=COLOR_BORDER)
    tf_f = flow_bar.text_frame
    tf_f.vertical_anchor = MSO_ANCHOR.MIDDLE
    p_f = tf_f.paragraphs[0]
    p_f.alignment = PP_ALIGN.CENTER
    p_f.text = "DATA FLOW:  User Prompt  ──▶  Virtual Thread Server  ──▶  TF-IDF & Gemini NLP  ──▶  Dual-Phase Engine  ──▶  Model DB  ──▶  Ranked Result + XAI"
    p_f.font.name = FONT_CODE
    p_f.font.size = Pt(9)
    p_f.font.bold = True
    p_f.font.color.rgb = COLOR_TEXT_LIGHT

    add_footer(slide)

def build_slide_5(prs):
    """SLIDE 5 — DATABASE DESIGN"""
    slide = prs.slides.add_slide(prs.slide_layouts[6])
    set_slide_background(slide)
    add_header(slide, "Database Design: Relational Schema & ER Architecture", "DATABASE DESIGN (REVIEW 1)", "05 / 10")

    # 4 Relational Tables representing the ModelMatch entity architecture
    card_w = Inches(5.68)
    card_h = Inches(2.05)
    start_x = Inches(0.8)
    gap_x = Inches(0.37)
    row1_y = Inches(1.5)
    row2_y = Inches(3.75)

    tables = [
        # Table 1: models
        ("TABLE: models", "Catalog of Frontier LLMs & Master Specs",
         [("model_id", "VARCHAR(50) [PK]", "Unique slug (e.g. 'claude-3-7-sonnet')"),
          ("name", "VARCHAR(100)", "Full commercial model name"),
          ("provider", "VARCHAR(50)", "Anthropic / OpenAI / Google / DeepSeek"),
          ("context_window_tokens", "INT", "Max context size (128K - 2M tokens)"),
          ("cost_input_per_m", "DECIMAL(10,4)", "Dollar pricing per 1M input tokens"),
          ("cost_output_per_m", "DECIMAL(10,4)", "Dollar pricing per 1M output tokens")]),

        # Table 2: model_capabilities
        ("TABLE: model_capabilities", "6D Normalized Capability Vectors",
         [("model_id", "VARCHAR(50) [PK, FK]", "References models.model_id (1:1 relation)"),
          ("reasoning_score", "DOUBLE (0-100)", "Logic, math & multi-step derivation"),
          ("coding_score", "DOUBLE (0-100)", "Syntax synthesis & SWE benchmarks"),
          ("context_score", "DOUBLE (0-100)", "Long-horizon memory & needle recall"),
          ("multimodal_score", "DOUBLE (0-100)", "Vision comprehension & chart parsing"),
          ("speed_score / cost_score", "DOUBLE (0-100)", "Throughput speed & cost efficiency")]),

        # Table 3: model_benchmarks
        ("TABLE: model_benchmarks", "Verified Ground-Truth Performance Citations",
         [("benchmark_id", "INT AUTO_INCREMENT [PK]", "Primary identifier"),
          ("model_id", "VARCHAR(50) [FK]", "References models.model_id (1:N relation)"),
          ("metric_name", "VARCHAR(50)", "SWE-bench Verified, MATH-500, MMLU-Pro"),
          ("score_value", "VARCHAR(50)", "Empirical benchmark percentage or Elo"),
          ("source_citation", "VARCHAR(255)", "Official research paper or public test suite")]),

        # Table 4: recommendation_logs
        ("TABLE: recommendation_logs", "Query Telemetry & Match History",
         [("query_id", "VARCHAR(36) [PK]", "UUID v4 for user transaction"),
          ("prompt_text", "TEXT", "User's natural language input string"),
          ("detected_category", "VARCHAR(50)", "Predicted category (Coding, Reasoning, etc.)"),
          ("top_model_id", "VARCHAR(50) [FK]", "References models.model_id of top winner"),
          ("match_score", "DOUBLE", "Final synthesized match percentage (0-100)"),
          ("created_at", "TIMESTAMP", "Audit trail & timestamp of query")])
    ]

    for idx, (tbl_title, tbl_sub, fields) in enumerate(tables):
        x = start_x if idx % 2 == 0 else start_x + card_w + gap_x
        y = row1_y if idx < 2 else row2_y

        card = create_card_shape(slide, x, y, card_w, card_h, bg_color=COLOR_CARD_BG, border_color=COLOR_BORDER_LIGHT)
        tf_t = card.text_frame
        tf_t.margin_left = Inches(0.18)
        tf_t.margin_right = Inches(0.18)
        tf_t.margin_top = Inches(0.12)

        p_t = tf_t.paragraphs[0]
        p_t.text = tbl_title
        p_t.font.name = FONT_CODE
        p_t.font.size = Pt(10.5)
        p_t.font.bold = True
        p_t.font.color.rgb = COLOR_ACCENT

        p_s = tf_t.add_paragraph()
        p_s.text = tbl_sub
        p_s.font.name = FONT_BODY
        p_s.font.size = Pt(8.5)
        p_s.font.color.rgb = COLOR_TEXT_MUTED
        p_s.space_after = Pt(4)

        for col_name, col_type, col_desc in fields:
            p_f = tf_t.add_paragraph()
            r_c = p_f.add_run()
            r_c.text = f"{col_name} "
            r_c.font.name = FONT_CODE
            r_c.font.size = Pt(8.5)
            r_c.font.bold = True
            r_c.font.color.rgb = COLOR_TEXT_WHITE

            r_t = p_f.add_run()
            r_t.text = f"[{col_type}] "
            r_t.font.name = FONT_BODY
            r_t.font.size = Pt(8.0)
            r_t.font.color.rgb = COLOR_ACCENT

            r_d = p_f.add_run()
            r_d.text = f"— {col_desc}"
            r_d.font.name = FONT_BODY
            r_d.font.size = Pt(8.0)
            r_d.font.color.rgb = COLOR_TEXT_LIGHT

    # Caption at bottom
    caption_card = create_card_shape(slide, Inches(0.8), Inches(6.0), Inches(11.73), Inches(0.65), bg_color=COLOR_CARD_BG, border_color=COLOR_BORDER)
    tf_cap = caption_card.text_frame
    tf_cap.vertical_anchor = MSO_ANCHOR.MIDDLE
    p_cap = tf_cap.paragraphs[0]
    p_cap.alignment = PP_ALIGN.CENTER
    r_cap1 = p_cap.add_run()
    r_cap1.text = "DATABASE INTEGRATION: "
    r_cap1.font.bold = True
    r_cap1.font.size = Pt(11)
    r_cap1.font.color.rgb = COLOR_ACCENT
    r_cap2 = p_cap.add_run()
    r_cap2.text = "“Structured model metadata and normalized benchmark vectors enable deterministic, data-driven recommendations.”"
    r_cap2.font.size = Pt(11)
    r_cap2.font.color.rgb = COLOR_TEXT_WHITE

    add_footer(slide)

def build_slide_6(prs):
    """SLIDE 6 — JDBC & JAVA BACKEND"""
    slide = prs.slides.add_slide(prs.slide_layouts[6])
    set_slide_background(slide)
    add_header(slide, "Java Backend Architecture & Database Connectivity", "JAVA BACKEND & JDBC", "06 / 10")

    # Left Column: Core Java Concepts Implemented
    create_card_shape(slide, Inches(0.8), Inches(1.5), Inches(5.6), Inches(4.3))
    left_box = slide.shapes.add_textbox(Inches(1.0), Inches(1.65), Inches(5.2), Inches(4.0))
    tf_l = left_box.text_frame
    tf_l.word_wrap = True

    p_lh = tf_l.paragraphs[0]
    p_lh.text = "Core Java Concepts in Project Code"
    p_lh.font.name = FONT_HEADING
    p_lh.font.size = Pt(14)
    p_lh.font.bold = True
    p_lh.font.color.rgb = COLOR_TEXT_WHITE

    java_features = [
        ("Java 21 Virtual Threads: ", "Executors.newVirtualThreadPerTaskExecutor() provides ultra-lightweight async concurrency for high-load REST handlers."),
        ("OOP & Encapsulation: ", "Immutable records and domain models (ModelEntity, TaskRequirements, PriorityWeights) enforce strict encapsulation."),
        ("Collections & Generics: ", "ConcurrentHashMap<String, ModelEntity>, EnumMap<TaskCategory, double[]>, and generic Stream pipelines for thread-safe access."),
        ("Defensive Error Handling: ", "Multi-tier fallback architecture: if external Gemini API is unreachable, local TF-IDF classifier ensures 100% continuous uptime."),
        ("Modular Layering: ", "Clean separation into controller, engine, classifier, service, repository, and server packages.")
    ]
    for feat_title, feat_desc in java_features:
        p_item = tf_l.add_paragraph()
        p_item.space_before = Pt(6)
        r_t = p_item.add_run()
        r_t.text = "• " + feat_title
        r_t.font.name = FONT_BODY
        r_t.font.size = Pt(10.5)
        r_t.font.bold = True
        r_t.font.color.rgb = COLOR_TEXT_WHITE

        r_d = p_item.add_run()
        r_d.text = feat_desc
        r_d.font.name = FONT_BODY
        r_d.font.size = Pt(10.0)
        r_d.font.color.rgb = COLOR_TEXT_LIGHT

    # Right Column: JDBC & DAO Pattern
    create_card_shape(slide, Inches(6.6), Inches(1.5), Inches(5.93), Inches(4.3))
    right_box = slide.shapes.add_textbox(Inches(6.85), Inches(1.65), Inches(5.4), Inches(4.0))
    tf_r = right_box.text_frame
    tf_r.word_wrap = True

    p_rh = tf_r.paragraphs[0]
    p_rh.text = "Database Connectivity Architecture (DAO Pattern)"
    p_rh.font.name = FONT_HEADING
    p_rh.font.size = Pt(14)
    p_rh.font.bold = True
    p_rh.font.color.rgb = COLOR_TEXT_WHITE

    dao_steps = [
        ("HTTP Controller (ApiHandler)", "Receives incoming JSON payload & extracts query parameters"),
        ("RecommendationService", "Applies business logic & coordinates ranking engine"),
        ("ModelDAO / Repository Interface", "Abstract Data Access Object isolating persistence logic"),
        ("JDBC Connection & PreparedStatement", "Parameterized SQL execution with SQL-injection immunity"),
        ("Relational Database / In-Memory Seed", "Atomic transaction persistence & concurrent thread read-safety")
    ]
    y_step = 2.15
    for i, (title, desc) in enumerate(dao_steps):
        s_card = create_card_shape(slide, Inches(6.9), Inches(y_step), Inches(5.33), Inches(0.62), bg_color=COLOR_CARD_BG_ALT, border_color=COLOR_BORDER_LIGHT)
        tf_s = s_card.text_frame
        tf_s.vertical_anchor = MSO_ANCHOR.MIDDLE
        tf_s.margin_left = Inches(0.18)
        p_st = tf_s.paragraphs[0]
        p_st.text = f"{i+1}. {title}"
        p_st.font.name = FONT_CODE
        p_st.font.size = Pt(9.5)
        p_st.font.bold = True
        p_st.font.color.rgb = COLOR_ACCENT
        p_sd = tf_s.add_paragraph()
        p_sd.text = desc
        p_sd.font.name = FONT_BODY
        p_sd.font.size = Pt(8.5)
        p_sd.font.color.rgb = COLOR_TEXT_LIGHT
        y_step += 0.73

    # Bottom Callout
    banner = create_card_shape(slide, Inches(0.8), Inches(6.0), Inches(11.73), Inches(0.65), bg_color=COLOR_CARD_BG, border_color=COLOR_ACCENT_BORDER)
    tf_b = banner.text_frame
    tf_b.vertical_anchor = MSO_ANCHOR.MIDDLE
    p_b = tf_b.paragraphs[0]
    p_b.alignment = PP_ALIGN.CENTER
    r_b1 = p_b.add_run()
    r_b1.text = "JAVA RUBRIC ALIGNMENT: "
    r_b1.font.bold = True
    r_b1.font.size = Pt(11)
    r_b1.font.color.rgb = COLOR_ACCENT
    r_b2 = p_b.add_run()
    r_b2.text = "Pure Java 21 implementation with zero bloated external web frameworks — showcasing raw Core Java mastery."
    r_b2.font.size = Pt(11)
    r_b2.font.color.rgb = COLOR_TEXT_WHITE

    add_footer(slide)

def build_slide_7(prs):
    """SLIDE 7 — RECOMMENDATION ENGINE ALGORITHM"""
    slide = prs.slides.add_slide(prs.slide_layouts[6])
    set_slide_background(slide)
    add_header(slide, "Recommendation Engine: Mathematical & Algorithmic Core", "RECOMMENDATION ALGORITHM", "07 / 10")

    # Left Column: Mathematical Formulas
    create_card_shape(slide, Inches(0.8), Inches(1.5), Inches(5.6), Inches(4.3))
    left_box = slide.shapes.add_textbox(Inches(1.0), Inches(1.65), Inches(5.2), Inches(4.0))
    tf_l = left_box.text_frame
    tf_l.word_wrap = True

    p_lh = tf_l.paragraphs[0]
    p_lh.text = "Dual-Phase Mathematical Formulations"
    p_lh.font.name = FONT_HEADING
    p_lh.font.size = Pt(14)
    p_lh.font.bold = True
    p_lh.font.color.rgb = COLOR_TEXT_WHITE

    math_sections = [
        ("1. Vector Cosine Similarity in R^6",
         "Sim(U, M) = (U · M) / (||U|| · ||M||) = Σ(u_i · m_i) / [sqrt(Σ u_i^2) · sqrt(Σ m_i^2)]\n"
         "Measures orientation alignment between user task vector U and model vector M."),

        ("2. Multi-Criteria Weighted Utility Scoring",
         "Score = w_r·R + w_c·C + w_x·X + w_m·M + w_s·S + w_p·P\n"
         "Normalized user slider weights (Σ w_i = 1.0) applied to Reasoning, Coding, Context, Vision, Speed, and Cost."),

        ("3. Constraint Multipliers (Φ)",
         "Φ_vision = 0.65 if task requires multimodal but model is text-only.\n"
         "Φ_context = 0.85 if task demands >200K tokens and model context < 200K."),

        ("4. Hybrid Synthesis Final Ranking",
         "FinalScore = [ 0.50 × WeightedScore + 0.50 × (CosineSim × 100) ] × Φ\n"
         "Blends geometric similarity with personalized utility priorities.")
    ]

    for title, formula in math_sections:
        p_sec = tf_l.add_paragraph()
        p_sec.space_before = Pt(6)
        r_t = p_sec.add_run()
        r_t.text = title + "\n"
        r_t.font.name = FONT_BODY
        r_t.font.size = Pt(10.5)
        r_t.font.bold = True
        r_t.font.color.rgb = COLOR_ACCENT

        r_f = p_sec.add_run()
        r_f.text = formula
        r_f.font.name = FONT_CODE
        r_f.font.size = Pt(9.0)
        r_f.font.color.rgb = COLOR_TEXT_LIGHT

    # Right Column: Live Illustrative Calculation Trace
    create_card_shape(slide, Inches(6.6), Inches(1.5), Inches(5.93), Inches(4.3))
    right_box = slide.shapes.add_textbox(Inches(6.85), Inches(1.65), Inches(5.4), Inches(4.0))
    tf_r = right_box.text_frame
    tf_r.word_wrap = True

    p_rh = tf_r.paragraphs[0]
    p_rh.text = "Algorithmic Trace (Actual Application Output)"
    p_rh.font.name = FONT_HEADING
    p_rh.font.size = Pt(14)
    p_rh.font.bold = True
    p_rh.font.color.rgb = COLOR_TEXT_WHITE

    p_rt = tf_r.add_paragraph()
    p_rt.text = "Task: 'Production REST API with complex reasoning, high code volume, 1M context, tight budget'"
    p_rt.font.name = FONT_BODY
    p_rt.font.size = Pt(10)
    p_rt.font.italic = True
    p_rt.font.color.rgb = COLOR_TEXT_LIGHT
    p_rt.space_after = Pt(8)

    sample_results = [
        ("#1 CLAUDE 3.7 SONNET", "Score: 92.7% | Cosine: 0.948 | Weighted: 90.6%", "Rank 1 Winner: Industry-leading SWE-bench Verified (70.3%) and dual-mode reasoning.", COLOR_ACCENT),
        ("#2 DEEPSEEK R1", "Score: 89.4% | Cosine: 0.912 | Weighted: 87.6%", "Strong open reasoning, MATH-500 champion (97.3%) with ultra-low token inference cost.", COLOR_TEXT_WHITE),
        ("#3 GEMINI 3.5 FLASH", "Score: 87.2% | Cosine: 0.895 | Weighted: 84.9%", "Unbeatable 1M token context capacity, 150 tok/s speed, and free tier accessibility.", COLOR_TEXT_WHITE),
        ("#4 QWEN 2.5 CODER 32B", "Score: 85.0% | Cosine: 0.880 | Weighted: 82.0%", "Specialized code synthesis (EvalPlus 88.4), privately hostable open weights.", COLOR_TEXT_WHITE)
    ]

    y_s = 2.45
    for rank_title, scores, why, title_col in sample_results:
        res_card = create_card_shape(slide, Inches(6.85), Inches(y_s), Inches(5.43), Inches(0.72), bg_color=COLOR_CARD_BG_ALT, border_color=COLOR_BORDER_LIGHT if title_col != COLOR_ACCENT else COLOR_ACCENT_BORDER)
        tf_res = res_card.text_frame
        tf_res.vertical_anchor = MSO_ANCHOR.MIDDLE
        tf_res.margin_left = Inches(0.18)
        p_rtitle = tf_res.paragraphs[0]
        r_rt1 = p_rtitle.add_run()
        r_rt1.text = rank_title + "  "
        r_rt1.font.name = FONT_HEADING
        r_rt1.font.size = Pt(10)
        r_rt1.font.bold = True
        r_rt1.font.color.rgb = title_col

        r_rt2 = p_rtitle.add_run()
        r_rt2.text = scores
        r_rt2.font.name = FONT_CODE
        r_rt2.font.size = Pt(8.5)
        r_rt2.font.color.rgb = COLOR_TEXT_MUTED

        p_rwhy = tf_res.add_paragraph()
        p_rwhy.text = why
        p_rwhy.font.name = FONT_BODY
        p_rwhy.font.size = Pt(8.5)
        p_rwhy.font.color.rgb = COLOR_TEXT_LIGHT
        y_s += 0.82

    # Bottom summary
    banner = create_card_shape(slide, Inches(0.8), Inches(6.0), Inches(11.73), Inches(0.65), bg_color=COLOR_CARD_BG, border_color=COLOR_BORDER)
    tf_b = banner.text_frame
    tf_b.vertical_anchor = MSO_ANCHOR.MIDDLE
    p_b = tf_b.paragraphs[0]
    p_b.alignment = PP_ALIGN.CENTER
    p_b.text = "KEY TAKEAWAY: The algorithm eliminates bias by combining geometrical vector cosine similarity with multi-criteria preference utility."
    p_b.font.name = FONT_HEADING
    p_b.font.size = Pt(11)
    p_b.font.bold = True
    p_b.font.color.rgb = COLOR_TEXT_WHITE

    add_footer(slide)

def build_slide_8(prs):
    """SLIDE 8 — USER INTERFACE & WORKING DEMO"""
    slide = prs.slides.add_slide(prs.slide_layouts[6])
    set_slide_background(slide)
    add_header(slide, "ModelMatch User Experience: Live Working Demo", "USER INTERFACE & DEMO", "08 / 10")

    # 4 UI Screenshot Cards cleanly framed
    col_w = Inches(2.78)
    gap_x = Inches(0.2)
    start_x = Inches(0.8)
    img_y = Inches(1.55)
    img_h = Inches(3.2)
    label_h = Inches(1.05)

    ui_frames = [
        ("slides_assets/home_screen.png", "1. Describe Your Task", "ChatGPT replica interface with zero layout shift, instant prompt input & suggestion chips."),
        ("slides_assets/weights_panel.png", "2. Priority Tuning Sliders", "6 normalized sliders (Reasoning, Coding, Context, Vision, Speed, Cost) & 1-click presets."),
        ("slides_assets/recommendation_result.png", "3. Ranked Winner & XAI", "Top match winner card with percentage score, 6D radar metrics, and token cost breakdown."),
        ("slides_assets/benchmark_matrix.png", "4. Empirical Matrix Modal", "Transparent database matrix citing SWE-bench, MATH-500, and MMLU-Pro for all 12 models.")
    ]

    for idx, (img_path, title, desc) in enumerate(ui_frames):
        x = start_x + idx * (col_w + gap_x)

        # Image Container Card
        card = create_card_shape(slide, x, img_y, col_w, img_h + label_h, bg_color=COLOR_CARD_BG, border_color=COLOR_BORDER_LIGHT)

        # Embed Image
        if os.path.exists(img_path):
            slide.shapes.add_picture(img_path, x + Inches(0.08), img_y + Inches(0.08), col_w - Inches(0.16), img_h - Inches(0.16))

        # Text below image inside card
        tb = slide.shapes.add_textbox(x + Inches(0.12), img_y + img_h, col_w - Inches(0.24), label_h)
        tf = tb.text_frame
        tf.word_wrap = True
        tf.margin_top = Inches(0.06)

        p_t = tf.paragraphs[0]
        p_t.text = title
        p_t.font.name = FONT_HEADING
        p_t.font.size = Pt(10.5)
        p_t.font.bold = True
        p_t.font.color.rgb = COLOR_ACCENT

        p_d = tf.add_paragraph()
        p_d.text = desc
        p_d.font.name = FONT_BODY
        p_d.font.size = Pt(8.5)
        p_d.font.color.rgb = COLOR_TEXT_LIGHT

    # Bottom Callout Bar
    banner = create_card_shape(slide, Inches(0.8), Inches(6.0), Inches(11.73), Inches(0.65), bg_color=COLOR_CARD_BG_ALT, border_color=COLOR_ACCENT_BORDER)
    tf_b = banner.text_frame
    tf_b.vertical_anchor = MSO_ANCHOR.MIDDLE
    p_b = tf_b.paragraphs[0]
    p_b.alignment = PP_ALIGN.CENTER
    r_b = p_b.add_run()
    r_b.text = "FRONTEND HIGHLIGHT: 100% Native HTML5/CSS/JS with zero external heavy UI frameworks — achieving 99+ Lighthouse performance."
    r_b.font.name = FONT_HEADING
    r_b.font.size = Pt(11)
    r_b.font.bold = True
    r_b.font.color.rgb = COLOR_TEXT_WHITE

    add_footer(slide)

def build_slide_9(prs):
    """SLIDE 9 — RESULTS & EXPLAINABILITY"""
    slide = prs.slides.add_slide(prs.slide_layouts[6])
    set_slide_background(slide)
    add_header(slide, "From Task → Recommendation: End-to-End Walkthrough", "RESULTS & EXPLAINABILITY", "09 / 10")

    # 4 Sequential Steps in the trace
    step_w = Inches(2.78)
    gap_x = Inches(0.2)
    start_x = Inches(0.8)
    step_y = Inches(1.5)
    step_h = Inches(4.3)

    trace_steps = [
        ("STAGE 1: INPUT", "User Task Prompt",
         "The user submits a complex real-world request:\n\n"
         "“I need to build an enterprise microservices backend in Java with complex business logic, async messaging, and low cloud compute cost.”\n\n"
         "• No technical keywords forced\n• Raw natural language format",
         COLOR_TEXT_WHITE),

        ("STAGE 2: ANALYSIS", "Feature Extraction",
         "The NLP Layer & TfIdfClassifier analyze requirements:\n\n"
         "• Category: Software Dev & Coding\n"
         "• Reasoning Demand: 0.90 / 1.0\n"
         "• Coding Demand: 0.95 / 1.0\n"
         "• Context Window: 0.70 / 1.0\n"
         "• Multimodal Demand: 0.20 / 1.0\n"
         "• Cost Sensitivity: 0.85 / 1.0",
         COLOR_TEXT_WHITE),

        ("STAGE 3: RANKING", "Leaderboard Output",
         "Engine computes hybrid scores across 12 models:\n\n"
         "#1 Claude 3.7 Sonnet — 94.2%\n"
         "   SWE-bench Verified (70.3%)\n\n"
         "#2 Qwen 2.5 Coder 32B — 89.1%\n"
         "   Open-source code champion\n\n"
         "#3 Gemini 3.5 Flash — 88.5%\n"
         "   Fastest speed, 1M context",
         COLOR_ACCENT),

        ("STAGE 4: EXPLAINABILITY", "Why This Model?",
         "ModelMatch generates transparent rationale:\n\n"
         "✓ Why Chosen:\n"
         "Industry-leading SWE-bench score & superior architectural refactoring.\n\n"
         "⚠ Trade-off Notice:\n"
         "Premium token cost; Qwen 2.5 recommended as low-budget fallback.\n\n"
         "• Estimated Cost: $0.045 / query",
         COLOR_ACCENT)
    ]

    for idx, (stage_tag, stage_title, content, highlight_col) in enumerate(trace_steps):
        x = start_x + idx * (step_w + gap_x)
        card = create_card_shape(slide, x, step_y, step_w, step_h, bg_color=COLOR_CARD_BG, border_color=COLOR_BORDER_LIGHT if highlight_col != COLOR_ACCENT else COLOR_ACCENT_BORDER)
        tf_s = card.text_frame
        tf_s.margin_left = Inches(0.16)
        tf_s.margin_right = Inches(0.16)
        tf_s.margin_top = Inches(0.18)

        p_tag = tf_s.paragraphs[0]
        p_tag.text = stage_tag
        p_tag.font.name = FONT_HEADING
        p_tag.font.size = Pt(9)
        p_tag.font.bold = True
        p_tag.font.color.rgb = highlight_col

        p_title = tf_s.add_paragraph()
        p_title.text = stage_title
        p_title.font.name = FONT_HEADING
        p_title.font.size = Pt(12)
        p_title.font.bold = True
        p_title.font.color.rgb = COLOR_TEXT_WHITE
        p_title.space_after = Pt(8)

        p_c = tf_s.add_paragraph()
        p_c.text = content
        p_c.font.name = FONT_BODY
        p_c.font.size = Pt(9.5)
        p_c.font.color.rgb = COLOR_TEXT_LIGHT

    # Bottom Callout Bar
    banner = create_card_shape(slide, Inches(0.8), Inches(6.0), Inches(11.73), Inches(0.65), bg_color=COLOR_CARD_BG, border_color=COLOR_ACCENT_BORDER)
    tf_b = banner.text_frame
    tf_b.vertical_anchor = MSO_ANCHOR.MIDDLE
    p_b = tf_b.paragraphs[0]
    p_b.alignment = PP_ALIGN.CENTER
    r_b = p_b.add_run()
    r_b.text = "CORE DISTINCTION: “ModelMatch does not only recommend an AI model—it explains the recommendation mathematically and transparently.”"
    r_b.font.name = FONT_HEADING
    r_b.font.size = Pt(11)
    r_b.font.bold = True
    r_b.font.color.rgb = COLOR_TEXT_WHITE

    add_footer(slide)

def build_slide_10(prs):
    """SLIDE 10 — CONCLUSION / FUTURE SCOPE"""
    slide = prs.slides.add_slide(prs.slide_layouts[6])
    set_slide_background(slide)
    add_header(slide, "Conclusion & Future Scope: The Road Ahead", "CONCLUSION & ROADMAP", "10 / 10")

    # Left Column: Project Summary & Review 1 Milestones
    create_card_shape(slide, Inches(0.8), Inches(1.5), Inches(5.6), Inches(4.3))
    left_box = slide.shapes.add_textbox(Inches(1.0), Inches(1.65), Inches(5.2), Inches(4.0))
    tf_l = left_box.text_frame
    tf_l.word_wrap = True

    p_lh = tf_l.paragraphs[0]
    p_lh.text = "Review 1 Accomplishments & Summary"
    p_lh.font.name = FONT_HEADING
    p_lh.font.size = Pt(14)
    p_lh.font.bold = True
    p_lh.font.color.rgb = COLOR_TEXT_WHITE

    summary_items = [
        ("Automated NLP Vectorization: ", "Successfully converts unstructured natural language into structured 6D mathematical requirement vectors."),
        ("Empirical Benchmark Grounding: ", "Replaces marketing hype with verified test scores (SWE-bench, MATH-500, MMLU-Pro)."),
        ("Dual-Phase Hybrid Engine: ", "Combines Cosine Angle Similarity with Multi-Criteria Utility preference scoring."),
        ("Transparent Explainability: ", "Provides match percentages, strength highlights, drawback notices, and query token pricing."),
        ("High-Performance Java 21: ", "Fully implemented with Virtual Threads concurrency, OOP encapsulation, and zero bloat."),
        ("ChatGPT Replica UI: ", "Production-grade dark minimalist web interface with live slider tuning and model comparisons.")
    ]

    for title, desc in summary_items:
        p_item = tf_l.add_paragraph()
        p_item.space_before = Pt(5)
        r_t = p_item.add_run()
        r_t.text = "✓ " + title
        r_t.font.name = FONT_BODY
        r_t.font.size = Pt(10.0)
        r_t.font.bold = True
        r_t.font.color.rgb = COLOR_ACCENT

        r_d = p_item.add_run()
        r_d.text = desc
        r_d.font.name = FONT_BODY
        r_d.font.size = Pt(9.5)
        r_d.font.color.rgb = COLOR_TEXT_LIGHT

    # Right Column: Future Scope & Roadmap (Review 2 & Beyond)
    create_card_shape(slide, Inches(6.6), Inches(1.5), Inches(5.93), Inches(4.3))
    right_box = slide.shapes.add_textbox(Inches(6.85), Inches(1.65), Inches(5.4), Inches(4.0))
    tf_r = right_box.text_frame
    tf_r.word_wrap = True

    p_rh = tf_r.paragraphs[0]
    p_rh.text = "Future Scope & Production Roadmap"
    p_rh.font.name = FONT_HEADING
    p_rh.font.size = Pt(14)
    p_rh.font.bold = True
    p_rh.font.color.rgb = COLOR_TEXT_WHITE

    roadmap_items = [
        ("Live Dynamic Benchmark Scraper: ", "Automated crawler syncing real-time LMSYS Chatbot Arena Elo and Artificial Analysis benchmarks."),
        ("Real-Time Token Pricing Feeds: ", "Continuous tracking of provider price changes, regional tier discounts, and batch pricing."),
        ("User Feedback Learning: ", "Bayesian preference adaptation updating model capability vectors based on thumbs-up feedback."),
        ("Automated Dynamic API Routing: ", "One-click execution proxy that automatically dispatches the user prompt directly to the winning LLM API."),
        ("Multi-Tenant Enterprise Auditing: ", "Organization-wide token usage tracking, historical audit logs, and spend optimization analytics.")
    ]

    for title, desc in roadmap_items:
        p_item = tf_r.add_paragraph()
        p_item.space_before = Pt(6)
        r_t = p_item.add_run()
        r_t.text = "➔ " + title
        r_t.font.name = FONT_BODY
        r_t.font.size = Pt(10.0)
        r_t.font.bold = True
        r_t.font.color.rgb = COLOR_TEXT_WHITE

        r_d = p_item.add_run()
        r_d.text = desc
        r_d.font.name = FONT_BODY
        r_d.font.size = Pt(9.5)
        r_d.font.color.rgb = COLOR_TEXT_LIGHT

    # Bottom Punchline with Logo
    closing_card = create_card_shape(slide, Inches(0.8), Inches(6.0), Inches(11.73), Inches(0.65), bg_color=COLOR_CARD_BG, border_color=COLOR_ACCENT_BORDER)

    logo_path = os.path.join("slides_assets", "logo.png")
    if os.path.exists(logo_path):
        slide.shapes.add_picture(logo_path, Inches(1.05), Inches(6.07), Inches(0.5), Inches(0.5))

    tf_close = closing_card.text_frame
    tf_close.vertical_anchor = MSO_ANCHOR.MIDDLE
    tf_close.margin_left = Inches(0.8)
    p_close = tf_close.paragraphs[0]
    r_c1 = p_close.add_run()
    r_c1.text = "“Don’t ask which AI is the best. "
    r_c1.font.bold = True
    r_c1.font.size = Pt(12)
    r_c1.font.color.rgb = COLOR_TEXT_LIGHT

    r_c2 = p_close.add_run()
    r_c2.text = "Ask which AI is best for THIS task.”"
    r_c2.font.bold = True
    r_c2.font.size = Pt(12)
    r_c2.font.color.rgb = COLOR_ACCENT

    add_footer(slide)

# ==============================================================================
# MAIN EXECUTION ROUTINE
# ==============================================================================
def main():
    print("Initializing ModelMatch Presentation Generator...")
    prs = create_deck()

    print("Building Slide 1: Title & Overview...")
    build_slide_1(prs)

    print("Building Slide 2: Problem Statement...")
    build_slide_2(prs)

    print("Building Slide 3: Proposed Solution...")
    build_slide_3(prs)

    print("Building Slide 4: System Architecture...")
    build_slide_4(prs)

    print("Building Slide 5: Database Design...")
    build_slide_5(prs)

    print("Building Slide 6: Java Backend & Database Connectivity...")
    build_slide_6(prs)

    print("Building Slide 7: Recommendation Algorithm...")
    build_slide_7(prs)

    print("Building Slide 8: User Interface & Working Demo...")
    build_slide_8(prs)

    print("Building Slide 9: Results & Explainability...")
    build_slide_9(prs)

    print("Building Slide 10: Conclusion & Future Scope...")
    build_slide_10(prs)

    output_file = "ModelMatch_Review1_Presentation.pptx"
    prs.save(output_file)
    print(f"SUCCESS: Presentation generated and saved to {output_file} (Total slides: {len(prs.slides)})")

if __name__ == "__main__":
    main()
