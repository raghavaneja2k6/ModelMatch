/* ==========================================================================
   ModelMatch — ChatGPT Replica UI Frontend Application Logic
   ========================================================================== */

(function () {
    'use strict';

    // State
    let currentHistory = [];
    let allModels = [];
    let customWeights = null; // null means adaptive
    let isProcessing = false;

    // DOM Elements
    const promptInput = document.getElementById('promptInput');
    const sendBtn = document.getElementById('sendBtn');
    const chatContainer = document.getElementById('chatContainer');
    const welcomeHero = document.getElementById('welcomeHero');
    const quickPromptChips = document.getElementById('quickPromptChips');
    const messagesThread = document.getElementById('messagesThread');
    const chatHistoryList = document.getElementById('chatHistoryList');
    const newChatBtn = document.getElementById('newChatBtn');
    const clearChatBtn = document.getElementById('clearChatBtn');
    const sidebar = document.getElementById('sidebar');
    const sidebarToggleBtn = document.getElementById('sidebarToggleBtn');
    const mobileSidebarToggle = document.getElementById('mobileSidebarToggle');

    // Weights Flyout Elements
    const toggleWeightsBtn = document.getElementById('toggleWeightsBtn');
    const weightsFlyout = document.getElementById('weightsFlyout');
    const closeWeightsFlyoutBtn = document.getElementById('closeWeightsFlyoutBtn');
    const openWeightsTopBtn = document.getElementById('openWeightsTopBtn');
    const openWeightsModalBtn = document.getElementById('openWeightsModalBtn');
    const resetWeightsBtn = document.getElementById('resetWeightsBtn');
    const weightsSumPill = document.getElementById('weightsSumPill');

    // Sliders
    const sliders = {
        reasoning: document.getElementById('sliderReasoning'),
        coding: document.getElementById('sliderCoding'),
        context: document.getElementById('sliderContext'),
        multimodal: document.getElementById('sliderMultimodal'),
        speed: document.getElementById('sliderSpeed'),
        cost: document.getElementById('sliderCost')
    };

    const valDisplays = {
        reasoning: document.getElementById('valReasoning'),
        coding: document.getElementById('valCoding'),
        context: document.getElementById('valContext'),
        multimodal: document.getElementById('valMultimodal'),
        speed: document.getElementById('valSpeed'),
        cost: document.getElementById('valCost')
    };

    // Modals
    const catalogModal = document.getElementById('catalogModal');
    const openCatalogBtn = document.getElementById('openCatalogBtn');
    const closeCatalogBtn = document.getElementById('closeCatalogBtn');
    const catalogTableBody = document.getElementById('catalogTableBody');
    const catalogSearchInput = document.getElementById('catalogSearchInput');

    const compareModal = document.getElementById('compareModal');
    const openCompareBtn = document.getElementById('openCompareBtn');
    const closeCompareBtn = document.getElementById('closeCompareBtn');
    const selectModelA = document.getElementById('selectModelA');
    const selectModelB = document.getElementById('selectModelB');
    const comparisonDisplay = document.getElementById('comparisonDisplay');

    const settingsModal = document.getElementById('settingsModal');
    const openSettingsBtn = document.getElementById('openSettingsBtn');
    const closeSettingsBtn = document.getElementById('closeSettingsBtn');
    const customApiKeyInput = document.getElementById('customApiKeyInput');
    const saveApiKeyBtn = document.getElementById('saveApiKeyBtn');

    // Initialize Application
    document.addEventListener('DOMContentLoaded', () => {
        initEventListeners();
        loadHistoryFromStorage();
        fetchAllModels();
        fetchSystemHealth();
    });

    function initEventListeners() {
        // Auto resize input without default scrollbars
        promptInput.addEventListener('input', () => {
            promptInput.style.height = '22px';
            const newHeight = Math.min(promptInput.scrollHeight, 180);
            promptInput.style.height = newHeight + 'px';
            promptInput.style.overflowY = promptInput.scrollHeight > 180 ? 'auto' : 'hidden';
            if (promptInput.value.trim().length > 0) {
                sendBtn.classList.add('active');
            } else {
                sendBtn.classList.remove('active');
            }
        });

        // Enter key to send (Shift+Enter for newline)
        promptInput.addEventListener('keydown', (e) => {
            if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                submitPrompt();
            }
        });

        sendBtn.addEventListener('click', submitPrompt);

        // Quick suggestion cards
        document.querySelectorAll('.suggestion-card').forEach(card => {
            card.addEventListener('click', () => {
                const prompt = card.getAttribute('data-prompt');
                promptInput.value = prompt;
                promptInput.style.height = 'auto';
                promptInput.style.height = Math.min(promptInput.scrollHeight, 200) + 'px';
                sendBtn.classList.add('active');
                submitPrompt();
            });
        });

        // Quick prompt chips above input bar
        document.querySelectorAll('.quick-chip').forEach(chip => {
            chip.addEventListener('click', () => {
                const prompt = chip.getAttribute('data-prompt');
                promptInput.value = prompt;
                promptInput.style.height = 'auto';
                promptInput.style.height = Math.min(promptInput.scrollHeight, 200) + 'px';
                sendBtn.classList.add('active');
                submitPrompt();
            });
        });

        // Sidebar toggles
        sidebarToggleBtn.addEventListener('click', () => {
            sidebar.classList.toggle('collapsed');
        });

        if (mobileSidebarToggle) {
            mobileSidebarToggle.addEventListener('click', () => {
                sidebar.classList.toggle('mobile-open');
            });
        }

        newChatBtn.addEventListener('click', startNewChat);
        clearChatBtn.addEventListener('click', startNewChat);

        // Weights flyout toggle
        const openFlyout = () => {
            weightsFlyout.classList.toggle('open');
            toggleWeightsBtn.classList.toggle('active');
        };
        toggleWeightsBtn.addEventListener('click', openFlyout);
        if (openWeightsTopBtn) openWeightsTopBtn.addEventListener('click', openFlyout);
        if (openWeightsModalBtn) openWeightsModalBtn.addEventListener('click', openFlyout);
        closeWeightsFlyoutBtn.addEventListener('click', () => {
            weightsFlyout.classList.remove('open');
            toggleWeightsBtn.classList.remove('active');
        });

        const doneWeightsBtn = document.getElementById('doneWeightsBtn');
        if (doneWeightsBtn) {
            doneWeightsBtn.addEventListener('click', () => {
                weightsFlyout.classList.remove('open');
                toggleWeightsBtn.classList.remove('active');
            });
        }

        // 1-Click Priority Presets
        document.querySelectorAll('.preset-chip').forEach(chip => {
            chip.addEventListener('click', () => {
                document.querySelectorAll('.preset-chip').forEach(c => c.classList.remove('active'));
                chip.classList.add('active');
                applyPreset(chip.getAttribute('data-preset'));
            });
        });

        // Slider listeners
        Object.keys(sliders).forEach(key => {
            sliders[key].addEventListener('input', () => {
                valDisplays[key].textContent = sliders[key].value + '%';
                // Reset active preset chips when user manually drags
                document.querySelectorAll('.preset-chip').forEach(c => c.classList.remove('active'));
                updateCustomWeightsState();
            });
        });

        resetWeightsBtn.addEventListener('click', resetWeightsToAdaptive);

        // Catalog modal
        openCatalogBtn.addEventListener('click', () => openModal(catalogModal));
        closeCatalogBtn.addEventListener('click', () => closeModal(catalogModal));
        catalogSearchInput.addEventListener('input', renderCatalogTable);

        // Compare modal
        openCompareBtn.addEventListener('click', () => {
            openModal(compareModal);
            updateCompareView();
        });
        closeCompareBtn.addEventListener('click', () => closeModal(compareModal));
        selectModelA.addEventListener('change', updateCompareView);
        selectModelB.addEventListener('change', updateCompareView);

        // Settings modal
        openSettingsBtn.addEventListener('click', () => openModal(settingsModal));
        closeSettingsBtn.addEventListener('click', () => closeModal(settingsModal));
        saveApiKeyBtn.addEventListener('click', updateApiKey);

        // Close modals on backdrop click
        document.querySelectorAll('.modal-backdrop').forEach(modal => {
            modal.addEventListener('click', (e) => {
                if (e.target === modal) closeModal(modal);
            });
        });
    }

    function openModal(modal) {
        modal.classList.add('open');
    }

    function closeModal(modal) {
        modal.classList.remove('open');
    }

    function applyPreset(type) {
        let p = { r: 25, c: 25, x: 15, m: 10, s: 10, p: 15 };
        if (type === 'coding') {
            p = { r: 25, c: 35, x: 15, m: 0, s: 10, p: 15 };
        } else if (type === 'reasoning') {
            p = { r: 45, c: 20, x: 15, m: 0, s: 10, p: 10 };
        } else if (type === 'budget') {
            p = { r: 10, c: 15, x: 5, m: 0, s: 30, p: 40 };
        } else if (type === 'context') {
            p = { r: 20, c: 0, x: 40, m: 10, s: 10, p: 20 };
        } else if (type === 'balanced') {
            p = { r: 17, c: 17, x: 17, m: 17, s: 16, p: 16 };
        }

        sliders.reasoning.value = p.r;
        sliders.coding.value = p.c;
        sliders.context.value = p.x;
        sliders.multimodal.value = p.m;
        sliders.speed.value = p.s;
        sliders.cost.value = p.p;

        Object.keys(valDisplays).forEach(key => {
            valDisplays[key].textContent = sliders[key].value + '%';
        });

        if (type === 'adaptive') {
            customWeights = null;
            weightsSumPill.textContent = 'Sum: 100% (Adaptive Defaults)';
        } else {
            updateCustomWeightsState();
        }
    }

    function updateCustomWeightsState() {
        const raw = {
            r: parseInt(sliders.reasoning.value),
            c: parseInt(sliders.coding.value),
            x: parseInt(sliders.context.value),
            m: parseInt(sliders.multimodal.value),
            s: parseInt(sliders.speed.value),
            p: parseInt(sliders.cost.value)
        };
        const sum = raw.r + raw.c + raw.x + raw.m + raw.s + raw.p;
        weightsSumPill.textContent = `Sum: ${sum}%`;

        // Normalize to decimal proportions for backend
        const norm = sum > 0 ? sum : 1;
        customWeights = {
            weightReasoning: raw.r / norm,
            weightCoding: raw.c / norm,
            weightContext: raw.x / norm,
            weightMultimodal: raw.m / norm,
            weightSpeed: raw.s / norm,
            weightCost: raw.p / norm
        };
    }

    function resetWeightsToAdaptive() {
        document.querySelectorAll('.preset-chip').forEach(c => c.classList.remove('active'));
        const autoChip = document.querySelector('.preset-chip[data-preset="adaptive"]');
        if (autoChip) autoChip.classList.add('active');
        applyPreset('adaptive');
    }

    function startNewChat() {
        welcomeHero.style.display = 'flex';
        if (quickPromptChips) quickPromptChips.style.display = 'none';
        messagesThread.innerHTML = '';
        promptInput.value = '';
        promptInput.style.height = '22px';
        promptInput.style.overflowY = 'hidden';
        sendBtn.classList.remove('active');
        weightsFlyout.classList.remove('open');
        toggleWeightsBtn.classList.remove('active');
    }

    // =========================================================================
    // SUBMIT & API CALL
    // =========================================================================
    async function submitPrompt() {
        const prompt = promptInput.value.trim();
        if (!prompt || isProcessing) return;

        isProcessing = true;
        sendBtn.disabled = true;

        // Hide hero welcome & show quick chips for subsequent testing
        welcomeHero.style.display = 'none';
        if (quickPromptChips) quickPromptChips.style.display = 'flex';

        // Append User Bubble
        appendUserMessage(prompt);

        // Clear input
        promptInput.value = '';
        promptInput.style.height = '22px';
        promptInput.style.overflowY = 'hidden';
        sendBtn.classList.remove('active');

        // Show typing indicator
        const typingEl = appendTypingIndicator();
        chatContainer.scrollTop = chatContainer.scrollHeight;

        try {
            const payload = {
                prompt: prompt,
                customWeights: customWeights,
                useGeminiAnalysis: true
            };

            const response = await fetch('/api/recommend', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            if (!response.ok) {
                throw new Error(`Server returned HTTP ${response.status}`);
            }

            const data = await response.json();

            // Remove typing indicator
            typingEl.remove();

            // Render Recommendation Card
            renderAssistantRecommendation(data);

            // Save to history
            saveQueryToHistory(prompt, data);

        } catch (err) {
            console.error(err);
            typingEl.remove();
            appendErrorMessage("Failed to obtain recommendation: " + err.message);
        } finally {
            isProcessing = false;
            sendBtn.disabled = false;
            chatContainer.scrollTop = chatContainer.scrollHeight;
        }
    }

    function appendUserMessage(text) {
        const row = document.createElement('div');
        row.className = 'message-row user-row';
        row.innerHTML = `<div class="user-bubble">${escapeHtml(text)}</div>`;
        messagesThread.appendChild(row);
    }

    function appendTypingIndicator() {
        const row = document.createElement('div');
        row.className = 'message-row';
        row.innerHTML = `
            <div class="assistant-avatar">
                <img src="logo-64.png" alt="ModelMatch AI" class="assistant-avatar-img" width="32" height="32" loading="lazy">
            </div>
            <div class="typing-bubble">
                <span class="typing-dot"></span>
                <span class="typing-dot"></span>
                <span class="typing-dot"></span>
            </div>
        `;
        messagesThread.appendChild(row);
        return row;
    }

    function appendErrorMessage(msg) {
        const row = document.createElement('div');
        row.className = 'message-row';
        row.innerHTML = `
            <div class="assistant-avatar" style="background:#ef4444;">!</div>
            <div class="user-bubble" style="background:rgba(239, 68, 68, 0.15); border:1px solid rgba(239, 68, 68, 0.4);">
                ${escapeHtml(msg)}
            </div>
        `;
        messagesThread.appendChild(row);
    }

    // =========================================================================
    // RENDER RECOMMENDATION ASSISTANT RESPONSE
    // =========================================================================
    function renderAssistantRecommendation(data) {
        const top = data.topModel;
        const model = top.model;
        const req = data.extractedRequirements;

        const row = document.createElement('div');
        row.className = 'message-row';

        let html = `
            <div class="assistant-avatar">
                <img src="logo-64.png" alt="ModelMatch AI" class="assistant-avatar-img" width="32" height="32" loading="lazy">
            </div>
            <div class="assistant-content">
                <!-- TOP WINNER HERO CARD -->
                <div class="winner-card">
                    <div class="winner-header-row">
                        <div>
                            <div class="winner-badges">
                                <span class="gold-rank-badge">🥇 #1 Top Match</span>
                                <span class="category-tag">🎯 Intent: ${escapeHtml(data.classifiedCategory)}</span>
                                <span class="category-tag highlight">${escapeHtml(model.badge || 'Benchmark Winner')}</span>
                            </div>
                            <div class="winner-title">${escapeHtml(model.name)}</div>
                            <div class="winner-provider">Provider: ${escapeHtml(model.provider)} &bull; ${escapeHtml(model.license)}</div>
                        </div>
                        <div class="winner-score-box">
                            <div class="winner-score-num">${top.finalScore}%</div>
                            <div class="winner-score-label">Match Score</div>
                        </div>
                    </div>

                    <!-- SPECS STRIP (EASY TO UNDERSTAND) -->
                    <div class="winner-specs-strip">
                        <div class="spec-cell">
                            <span class="spec-cell-label">Context Window</span>
                            <span class="spec-cell-value">${formatTokens(model.contextWindowTokens)}</span>
                            <span class="spec-subtext">${getContextSubtext(model.contextWindowTokens)}</span>
                        </div>
                        <div class="spec-cell">
                            <span class="spec-cell-label">Inference Speed</span>
                            <span class="spec-cell-value">~${model.speedTokensPerSec} tok/s</span>
                            <span class="spec-subtext">${getSpeedBadge(model.speedTokensPerSec)}</span>
                        </div>
                        <div class="spec-cell">
                            <span class="spec-cell-label">Pricing (In / Out)</span>
                            <span class="spec-cell-value">$${model.inputCostPer1M.toFixed(3)} / $${model.outputCostPer1M.toFixed(2)}</span>
                            <span class="spec-subtext">${getPricingTier(model.inputCostPer1M)}</span>
                        </div>
                        <div class="spec-cell">
                            <span class="spec-cell-label">Requirement Match</span>
                            <span class="spec-cell-value">${Math.round(top.cosineSimilarity * 100)}%</span>
                            <span class="spec-subtext">Semantic Alignment</span>
                        </div>
                    </div>

                    <!-- WHY RECOMMENDED -->
                    <div class="explain-section">
                        <div class="explain-section-title">
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#10a37f" stroke-width="2.5"><polyline points="20 6 9 17 4 12"></polyline></svg>
                            Why this model fits your requirements:
                        </div>
                        <ul class="explain-bullet-list">
                            ${top.matchHighlights.map(h => `
                                <li class="explain-bullet-item">
                                    <span class="bullet-check-icon">&#10003;</span>
                                    <span>${escapeHtml(h)}</span>
                                </li>
                            `).join('')}
                        </ul>
                    </div>

                    <!-- POTENTIAL DRAWBACKS -->
                    ${top.potentialDrawbacks && top.potentialDrawbacks.length > 0 ? `
                        <div class="drawback-box">
                            <div class="drawback-title">
                                <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"></path><line x1="12" y1="9" x2="12" y2="13"></line><line x1="12" y1="17" x2="12.01" y2="17"></line></svg>
                                Potential Tradeoffs / Watch-outs:
                            </div>
                            <div class="drawback-text">${escapeHtml(top.potentialDrawbacks.join('. '))}</div>
                        </div>
                    ` : ''}
                </div>

                <!-- GEMINI 3.5 FLASH DEEP COMMENTARY -->
                ${data.geminiAiInsights ? `
                    <div class="gemini-insight-box">
                        <div class="gemini-insight-header">
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"></circle><line x1="12" y1="16" x2="12" y2="12"></line><line x1="12" y1="8" x2="12.01" y2="8"></line></svg>
                            <span>Gemini 3.5 Flash Technical Assessment</span>
                        </div>
                        <div class="gemini-insight-text">${escapeHtml(data.geminiAiInsights)}</div>
                    </div>
                ` : ''}

                <!-- DIMENSION COMPARISON BARS -->
                <div class="vector-breakdown-card">
                    <div class="vector-breakdown-header">
                        <span>Requirements vs Capability Breakdown</span>
                        <span style="font-size:12px;color:var(--text-muted);">Model Capability (Solid) vs What Your Task Needed (Marker)</span>
                    </div>
                    <div class="dimension-bars-list">
                        ${renderDimensionBar("Reasoning & Logic", req.reasoning * 100, model.reasoningScore, "reasoning")}
                        ${renderDimensionBar("Coding & Syntax", req.coding * 100, model.codingScore, "coding")}
                        ${renderDimensionBar("Context Window", req.context * 100, model.contextScore, "context")}
                        ${renderDimensionBar("Multimodal / Vision", req.multimodal * 100, model.multimodalScore, "multimodal")}
                        ${renderDimensionBar("Speed & Latency", req.speed * 100, model.speedScore, "speed")}
                        ${renderDimensionBar("Cost Efficiency", req.cost * 100, model.costScore, "cost")}
                    </div>
                </div>

                <!-- FORMULA ACCORDION -->
                <div class="formula-accordion">
                    <button class="formula-accordion-trigger" onclick="this.nextElementSibling.classList.toggle('open')">
                        <span>🔬 Deep Dive: Scoring Math & Technical Formulas (Click to expand)</span>
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="6 9 12 15 18 9"></polyline></svg>
                    </button>
                    <div class="formula-body">
                        <pre style="white-space:pre-wrap;margin:0;">${escapeHtml(data.formulaBreakdown)}</pre>
                    </div>
                </div>

                <!-- RANKED ALTERNATIVES -->
                ${data.rankedAlternatives && data.rankedAlternatives.length > 0 ? `
                    <div class="alternatives-card">
                        <div class="alternatives-title">Ranked Alternatives & Runners-up</div>
                        <div class="alternatives-list">
                            ${data.rankedAlternatives.map(alt => `
                                <div class="alt-row">
                                    <div class="alt-left">
                                        <span class="alt-rank">#${alt.rank}</span>
                                        <div>
                                            <div class="alt-name">${escapeHtml(alt.model.name)}</div>
                                            <div class="alt-provider">${escapeHtml(alt.model.provider)} &bull; ${escapeHtml(alt.model.benchmarkSource)}</div>
                                        </div>
                                    </div>
                                    <div class="alt-right">
                                        <div class="alt-score">${alt.finalScore}%</div>
                                        <button class="alt-compare-btn" onclick="window.compareWithTop('${model.id}', '${alt.model.id}')">Compare</button>
                                    </div>
                                </div>
                            `).join('')}
                        </div>
                    </div>
                ` : ''}

                <!-- INTERACTIVE TOKEN COST ESTIMATOR -->
                <div class="cost-calculator-card">
                    <div class="cost-calculator-title">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#10a37f" stroke-width="2"><circle cx="12" cy="12" r="10"></circle><line x1="12" y1="8" x2="12" y2="12"></line><line x1="12" y1="16" x2="12.01" y2="16"></line></svg>
                        <span>Interactive Cost Estimator for ${escapeHtml(model.name)}</span>
                    </div>
                    <div class="cost-presets-row">
                        <span class="cost-preset-label">Quick Tokens:</span>
                        <button type="button" class="cost-preset-btn" onclick="window.setCostTokens(this, 10000, ${model.inputCostPer1M}, ${model.outputCostPer1M})">10K (Short Script)</button>
                        <button type="button" class="cost-preset-btn" onclick="window.setCostTokens(this, 50000, ${model.inputCostPer1M}, ${model.outputCostPer1M})">50K (Medium File)</button>
                        <button type="button" class="cost-preset-btn active" onclick="window.setCostTokens(this, 100000, ${model.inputCostPer1M}, ${model.outputCostPer1M})">100K (Module)</button>
                        <button type="button" class="cost-preset-btn" onclick="window.setCostTokens(this, 500000, ${model.inputCostPer1M}, ${model.outputCostPer1M})">500K (Entire Repo)</button>
                    </div>
                    <div class="cost-slider-group">
                        <label style="font-size:12px;color:var(--text-muted);">Estimated Total Tokens:</label>
                        <input type="range" class="cost-slider" min="1000" max="500000" step="5000" value="100000" oninput="window.updateCostDisplay(this.value, ${model.inputCostPer1M}, ${model.outputCostPer1M}, this.nextElementSibling)">
                        <span class="cost-result-pill">~$${(((100000 * 0.7) / 1000000 * model.inputCostPer1M) + ((100000 * 0.3) / 1000000 * model.outputCostPer1M)).toFixed(4)} (100k tok)</span>
                    </div>
                </div>
            </div>
        `;

        row.innerHTML = html;
        messagesThread.appendChild(row);
    }

    function renderDimensionBar(title, reqVal, modelVal, colorClass) {
        const req = Math.round(reqVal);
        const mod = Math.round(modelVal);
        let badgeHtml = '';
        if (mod >= req + 10) {
            badgeHtml = '<span class="dim-badge badge-exceeds">★ Exceeds Need</span>';
        } else if (mod >= req - 8) {
            badgeHtml = '<span class="dim-badge badge-meets">✓ Meets Need</span>';
        } else {
            badgeHtml = '<span class="dim-badge badge-below">Trade-off</span>';
        }

        return `
            <div class="dimension-bar-row">
                <div class="dim-labels">
                    <span class="dim-title">${title} ${badgeHtml}</span>
                    <span class="dim-vals">Required: <strong>${req}%</strong> | Model: <strong>${mod}%</strong></span>
                </div>
                <div class="dim-track">
                    <div class="dim-fill ${colorClass}" style="width: ${Math.min(100, mod)}%;"></div>
                    <div class="dim-marker" style="left: ${Math.min(98, Math.max(2, req))}%;" title="Target Needed: ${req}%"></div>
                </div>
            </div>
        `;
    }

    window.setCostTokens = function (btn, tokens, inPrice, outPrice) {
        const parent = btn.closest('.cost-calculator-card');
        if (!parent) return;
        parent.querySelectorAll('.cost-preset-btn').forEach(b => b.classList.remove('active'));
        btn.classList.add('active');
        const slider = parent.querySelector('.cost-slider');
        const pill = parent.querySelector('.cost-result-pill');
        if (slider) slider.value = tokens;
        if (pill) window.updateCostDisplay(tokens, inPrice, outPrice, pill);
    };

    // =========================================================================
    // COST CALCULATOR HELPER
    // =========================================================================
    window.updateCostDisplay = function (tokens, inPrice, outPrice, pillEl) {
        const t = parseInt(tokens);
        // Assume 70% input, 30% output split
        const cost = ((t * 0.7) / 1000000 * inPrice) + ((t * 0.3) / 1000000 * outPrice);
        const tokLabel = t >= 1000 ? (t / 1000) + 'k' : t;
        pillEl.textContent = `~$${cost.toFixed(4)} (${tokLabel} tok)`;
    };

    // =========================================================================
    // MODEL CATALOG & BENCHMARK MATRIX
    // =========================================================================
    async function fetchAllModels() {
        try {
            const res = await fetch('/api/models');
            if (res.ok) {
                allModels = await res.json();
                renderCatalogTable();
                populateCompareDropdowns();
            }
        } catch (err) {
            console.error('Failed to load models:', err);
        }
    }

    function renderCatalogTable() {
        if (!catalogTableBody) return;
        const q = (catalogSearchInput.value || '').toLowerCase();
        const filtered = allModels.filter(m =>
            m.name.toLowerCase().includes(q) ||
            m.provider.toLowerCase().includes(q) ||
            m.benchmarkSource.toLowerCase().includes(q)
        );

        catalogTableBody.innerHTML = filtered.map(m => `
            <tr>
                <td><strong>${escapeHtml(m.name)}</strong></td>
                <td>${escapeHtml(m.provider)}</td>
                <td><span style="color:#8b5cf6;font-weight:600;">${m.reasoningScore}</span></td>
                <td><span style="color:#3b82f6;font-weight:600;">${m.codingScore}</span></td>
                <td>${formatNumber(m.contextWindowTokens)}</td>
                <td>${m.multimodalScore >= 50 ? '<span style="color:#10a37f;">Yes</span>' : '<span style="color:#ef4444;">No</span>'}</td>
                <td>${m.speedTokensPerSec} tps</td>
                <td>$${m.inputCostPer1M.toFixed(3)}</td>
                <td>$${m.outputCostPer1M.toFixed(2)}</td>
                <td style="font-size:11.5px;color:var(--text-muted);">${escapeHtml(m.benchmarkSource)}</td>
            </tr>
        `).join('');
    }

    // =========================================================================
    // HEAD-TO-HEAD COMPARISON
    // =========================================================================
    function populateCompareDropdowns() {
        if (!selectModelA || !selectModelB) return;
        selectModelA.innerHTML = '';
        selectModelB.innerHTML = '';

        allModels.forEach((m, idx) => {
            const optA = document.createElement('option');
            optA.value = m.id;
            optA.textContent = `${m.name} (${m.provider})`;
            if (idx === 0) optA.selected = true;
            selectModelA.appendChild(optA);

            const optB = document.createElement('option');
            optB.value = m.id;
            optB.textContent = `${m.name} (${m.provider})`;
            if (idx === 1 || (idx === 0 && allModels.length === 1)) optB.selected = true;
            selectModelB.appendChild(optB);
        });

        updateCompareView();
    }

    window.compareWithTop = function (idA, idB) {
        if (selectModelA && selectModelB) {
            selectModelA.value = idA;
            selectModelB.value = idB;
            openModal(compareModal);
            updateCompareView();
        }
    };

    function updateCompareView() {
        if (!comparisonDisplay) return;
        const idA = selectModelA.value;
        const idB = selectModelB.value;
        const mA = allModels.find(m => m.id === idA);
        const mB = allModels.find(m => m.id === idB);
        if (!mA || !mB) return;

        const badgeA = (vA, vB, isLowerBetter = false) => {
            if (vA === vB) return '';
            const win = isLowerBetter ? vA < vB : vA > vB;
            return win ? '<span class="comp-win-pill">★ Better</span>' : '';
        };

        comparisonDisplay.innerHTML = `
            <div class="comparison-grid">
                <div class="compare-card">
                    <div class="compare-title">${escapeHtml(mA.name)}</div>
                    <div class="compare-provider">${escapeHtml(mA.provider)} &bull; ${escapeHtml(mA.license)}</div>
                    <div class="compare-metric-row"><span>Reasoning:</span><strong>${mA.reasoningScore} / 100 ${badgeA(mA.reasoningScore, mB.reasoningScore)}</strong></div>
                    <div class="compare-metric-row"><span>Coding:</span><strong>${mA.codingScore} / 100 ${badgeA(mA.codingScore, mB.codingScore)}</strong></div>
                    <div class="compare-metric-row"><span>Context Window:</span><strong>${formatTokens(mA.contextWindowTokens)} ${badgeA(mA.contextWindowTokens, mB.contextWindowTokens)}</strong></div>
                    <div class="compare-metric-row"><span>Vision / Multimodal:</span><strong>${mA.multimodalScore >= 50 ? 'Supported' : 'Text Only'} ${badgeA(mA.multimodalScore, mB.multimodalScore)}</strong></div>
                    <div class="compare-metric-row"><span>Speed:</span><strong>~${mA.speedTokensPerSec} tok/s ${badgeA(mA.speedTokensPerSec, mB.speedTokensPerSec)}</strong></div>
                    <div class="compare-metric-row"><span>Input / 1M:</span><strong>$${mA.inputCostPer1M.toFixed(3)} ${badgeA(mA.inputCostPer1M, mB.inputCostPer1M, true)}</strong></div>
                    <div class="compare-metric-row"><span>Output / 1M:</span><strong>$${mA.outputCostPer1M.toFixed(2)} ${badgeA(mA.outputCostPer1M, mB.outputCostPer1M, true)}</strong></div>
                </div>

                <div class="compare-card">
                    <div class="compare-title">${escapeHtml(mB.name)}</div>
                    <div class="compare-provider">${escapeHtml(mB.provider)} &bull; ${escapeHtml(mB.license)}</div>
                    <div class="compare-metric-row"><span>Reasoning:</span><strong>${mB.reasoningScore} / 100 ${badgeA(mB.reasoningScore, mA.reasoningScore)}</strong></div>
                    <div class="compare-metric-row"><span>Coding:</span><strong>${mB.codingScore} / 100 ${badgeA(mB.codingScore, mA.codingScore)}</strong></div>
                    <div class="compare-metric-row"><span>Context Window:</span><strong>${formatTokens(mB.contextWindowTokens)} ${badgeA(mB.contextWindowTokens, mA.contextWindowTokens)}</strong></div>
                    <div class="compare-metric-row"><span>Vision / Multimodal:</span><strong>${mB.multimodalScore >= 50 ? 'Supported' : 'Text Only'} ${badgeA(mB.multimodalScore, mA.multimodalScore)}</strong></div>
                    <div class="compare-metric-row"><span>Speed:</span><strong>~${mB.speedTokensPerSec} tok/s ${badgeA(mB.speedTokensPerSec, mA.speedTokensPerSec)}</strong></div>
                    <div class="compare-metric-row"><span>Input / 1M:</span><strong>$${mB.inputCostPer1M.toFixed(3)} ${badgeA(mB.inputCostPer1M, mA.inputCostPer1M, true)}</strong></div>
                    <div class="compare-metric-row"><span>Output / 1M:</span><strong>$${mB.outputCostPer1M.toFixed(2)} ${badgeA(mB.outputCostPer1M, mA.outputCostPer1M, true)}</strong></div>
                </div>
            </div>
        `;
    }

    // =========================================================================
    // SYSTEM SETTINGS & API KEY
    // =========================================================================
    async function fetchSystemHealth() {
        try {
            const res = await fetch('/api/health');
            if (res.ok) {
                const data = await res.json();
                const jEl = document.getElementById('specJavaVersion');
                const kEl = document.getElementById('specApiKeyMasked');
                if (jEl && data.javaVersion) jEl.textContent = 'Java ' + data.javaVersion;
                if (kEl) kEl.textContent = data.geminiConfigured ? 'Connected (Server-Side)' : 'Standby (Offline Mode)';
            }
        } catch (e) {
            console.error('Health fetch error:', e);
        }
    }

    async function updateApiKey() {
        const key = customApiKeyInput.value.trim();
        if (!key) return;
        try {
            const res = await fetch('/api/config', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ apiKey: key })
            });
            if (res.ok) {
                alert('Gemini API key updated successfully!');
                customApiKeyInput.value = '';
                fetchSystemHealth();
            }
        } catch (err) {
            alert('Failed to update API key: ' + err.message);
        }
    }

    // =========================================================================
    // HISTORY STORAGE
    // =========================================================================
    function loadHistoryFromStorage() {
        try {
            const raw = localStorage.getItem('modelmatch_history');
            if (raw) {
                currentHistory = JSON.parse(raw);
                renderHistorySidebar();
            }
        } catch (e) {
            currentHistory = [];
        }
    }

    function saveQueryToHistory(prompt, data) {
        const item = {
            id: Date.now().toString(),
            title: prompt.length > 36 ? prompt.substring(0, 36) + '...' : prompt,
            prompt: prompt,
            topModel: data.topModel.model.name,
            score: data.topModel.finalScore,
            category: data.classifiedCategory,
            timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
            data: data
        };

        currentHistory.unshift(item);
        if (currentHistory.length > 25) currentHistory.pop();
        localStorage.setItem('modelmatch_history', JSON.stringify(currentHistory));
        renderHistorySidebar();
    }

    function renderHistorySidebar() {
        if (!chatHistoryList) return;
        chatHistoryList.innerHTML = currentHistory.map(item => `
            <div class="chat-history-item" onclick="window.loadSavedSession('${item.id}')">
                <span class="history-item-title">${escapeHtml(item.title)}</span>
                <button class="history-delete-btn" onclick="event.stopPropagation(); window.deleteHistoryItem('${item.id}')" title="Delete">&times;</button>
            </div>
        `).join('');
    }

    window.loadSavedSession = function (id) {
        const found = currentHistory.find(i => i.id === id);
        if (!found) return;
        welcomeHero.style.display = 'none';
        if (quickPromptChips) quickPromptChips.style.display = 'flex';
        messagesThread.innerHTML = '';
        appendUserMessage(found.prompt);
        renderAssistantRecommendation(found.data);
        chatContainer.scrollTop = chatContainer.scrollHeight;
    };

    window.deleteHistoryItem = function (id) {
        currentHistory = currentHistory.filter(i => i.id !== id);
        localStorage.setItem('modelmatch_history', JSON.stringify(currentHistory));
        renderHistorySidebar();
    };

    // =========================================================================
    // UTILITIES
    // =========================================================================
    function escapeHtml(str) {
        if (!str) return '';
        return String(str)
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#39;');
    }

    function formatNumber(num) {
        if (!num) return '0';
        if (num >= 1900000) return '2M';
        if (num >= 950000) return '1M';
        if (num >= 1000) return Math.round(num / 1000) + 'K';
        return num.toString();
    }

    function formatTokens(tokens) {
        if (!tokens) return '0 tokens';
        if (tokens >= 1900000) return '2M tokens';
        if (tokens >= 950000) return '1M tokens';
        if (tokens >= 1000) return Math.round(tokens / 1000) + 'K tokens';
        return tokens.toLocaleString() + ' tokens';
    }

    function getContextSubtext(tokens) {
        if (tokens >= 1900000) return '~1.5M words (Full codebase)';
        if (tokens >= 950000) return '~750k words (Entire repo)';
        if (tokens >= 200000) return '~150k words (Multi-file)';
        if (tokens >= 128000) return '~95k words (Large document)';
        return 'Standard context';
    }

    function getSpeedBadge(tokPerSec) {
        if (tokPerSec >= 120) return '⚡ Ultra Fast (Sub-sec)';
        if (tokPerSec >= 80) return '⚡ High Throughput';
        return 'Standard Latency';
    }

    function getPricingTier(inCost) {
        if (inCost < 0.20) return '💰 Ultra Low Cost';
        if (inCost < 1.00) return '💵 Budget Friendly';
        return '💎 Premium Tier';
    }

})();
