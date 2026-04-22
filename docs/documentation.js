const docs = [
  {
    file: "admin_approval_system.md",
    title: "Admin-Based Operator Approval",
    heading: "Admin-Based Operator Approval System - Implementation Summary",
    category: "Operations",
    summary: "Explains role-based approval flow for onboarding and managing operators."
  },
  {
    file: "admin_setup.md",
    title: "Admin User Setup",
    heading: "Admin User Setup Instructions",
    category: "Operations",
    summary: "Covers the process to create and configure admin users safely."
  },
  {
    file: "API_testing_guide.md",
    title: "API Testing Guide",
    heading: "Cargo Management System - API Testing Guide",
    category: "Backend",
    summary: "Documents endpoint validation, request patterns, and testing workflows."
  },
  {
    file: "aiven_kafka.md",
    title: "Aiven Kafka Deployment",
    heading: "Kafka Deployment to Aiven with Backend Integration",
    category: "Infrastructure",
    summary: "Details how Kafka is deployed on Aiven and connected to backend services."
  },
  {
    file: "backend_hld_details.md",
    title: "Backend HLD",
    heading: "Backend High-Level Design (HLD)",
    category: "Architecture",
    summary: "Describes major backend components, responsibilities, and system boundaries."
  },
  {
    file: "backend_workflow.md",
    title: "Backend Workflow",
    heading: "Backend Development Workflow",
    category: "Architecture",
    summary: "Defines backend development lifecycle, quality gates, and handoff flow."
  },
  {
    file: "core_database_entities.md",
    title: "Core Database Entities",
    heading: "Core Database Entities for Supply Chain Cargo Management System",
    category: "Database",
    summary: "Explains key domain entities and their relationships for CargoFlow data modeling."
  },
  {
    file: "db_basic_schema.md",
    title: "Basic Database Schema",
    heading: "Basic DB Schema Draft from Updated ERD",
    category: "Database",
    summary: "Presents foundational schema tables and early structure from ERD groundwork."
  },
  {
    file: "er_diagram_explanation.md",
    title: "ER Diagram Explanation",
    heading: "ER Diagram Explanation - Supply Chain Cargo Management",
    category: "Database",
    summary: "Breaks down ER diagram choices and cardinality decisions."
  },
  {
    file: "instructions.md",
    title: "Project Instructions",
    heading: "Supply Chain Cargo Management System",
    category: "Product",
    summary: "Central instruction set for project goals, expectations, and implementation scope."
  },
  {
    file: "kafka_design_and_usage.md",
    title: "Kafka Design and Usage",
    heading: "Kafka Design and Usage Guide",
    category: "Infrastructure",
    summary: "Documents event architecture, producer-consumer behavior, and integration strategy."
  },
  {
    file: "lld_api_outline.md",
    title: "API LLD Outline",
    heading: "API Low-Level Design (LLD) Outline",
    category: "Backend",
    summary: "Provides endpoint-level low-level design for implementation details."
  },
  {
    file: "lld_db_queries.md",
    title: "Database Query LLD",
    heading: "Low-Level Design (LLD) for Database Queries",
    category: "Database",
    summary: "Contains SQL-level query strategy and performance-aware data access patterns."
  },
  {
    file: "non_functional_reqs.md",
    title: "Non-Functional Requirements",
    heading: "Non-Functional Requirements (NFRs)",
    category: "Product",
    summary: "Covers reliability, security, scalability, and other non-functional constraints."
  },
  {
    file: "project_roadmap_hld.md",
    title: "Project Roadmap and HLD",
    heading: "Project: Supply Chain Cargo Management - Roadmap and HLD",
    category: "Product",
    summary: "Maps product phases and high-level architecture goals over the delivery timeline."
  },
  {
    file: "route_pages_structure.md",
    title: "Route Pages Structure",
    heading: "Route Management - Page Structure",
    category: "Frontend",
    summary: "Explains UI page composition and structure for route management screens."
  },
  {
    file: "schema_refinements.md",
    title: "Schema Refinements",
    heading: "DB Schema Refinements on Basics",
    category: "Database",
    summary: "Tracks refinements applied after the base schema to improve consistency and depth."
  },
  {
    file: "system_integration_lld.md",
    title: "System Integration LLD",
    heading: "Low-Level Design (LLD): System Integration",
    category: "Architecture",
    summary: "Defines integration behavior between services, data layer, and external systems."
  },
  {
    file: "ui_high_fid.md",
    title: "UI High Fidelity",
    heading: "High-Fidelity UI (5 Pages) - Supply Chain Cargo Management",
    category: "Frontend",
    summary: "Describes polished UI flows, visual intent, and high-fidelity page designs."
  },
  {
    file: "ui_wireframes.md",
    title: "UI Wireframes",
    heading: "UI Wireframes and Prototype - Supply Chain Cargo Management",
    category: "Frontend",
    summary: "Contains early wireframes and interaction ideas used to validate UX direction."
  }
];

const state = {
  activeDoc: null,
  query: ""
};
const REPO_BASE_URL = "https://github.com/dhairyajangir/cargoflow";

const navList = document.getElementById("docNavList");
const cardsContainer = document.getElementById("docCards");
const searchInput = document.getElementById("docSearch");
const docStats = document.getElementById("docStats");
const viewerCategory = document.getElementById("viewerCategory");
const viewerTitle = document.getElementById("viewerTitle");
const viewerSource = document.getElementById("viewerSource");
const viewerSummary = document.getElementById("viewerSummary");
const viewerBody = document.getElementById("viewerBody");

function normalize(text) {
  return text.toLowerCase().trim();
}

function getFilteredDocs() {
  if (!state.query) {
    return docs;
  }

  return docs.filter((doc) => {
    const haystack = [doc.file, doc.title, doc.heading, doc.category, doc.summary]
      .join(" ")
      .toLowerCase();
    return haystack.includes(state.query);
  });
}

function escapeHtml(text) {
  return text
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/\"/g, "&quot;")
    .replace(/'/g, "&#039;");
}

function inlineMarkdown(text) {
  return escapeHtml(text)
    .replace(/`([^`]+)`/g, "<code>$1</code>")
    .replace(/\*\*([^*]+)\*\*/g, "<strong>$1</strong>")
    .replace(/\*([^*]+)\*/g, "<em>$1</em>")
    .replace(/\[([^\]]+)\]\(([^)]+)\)/g, '<a href="$2" target="_blank" rel="noreferrer">$1</a>');
}

function markdownToHtml(markdownText) {
  const lines = markdownText.replace(/\r/g, "").split("\n");
  const html = [];

  let inCode = false;
  let codeBuffer = [];
  let listType = null;

  const closeListIfNeeded = () => {
    if (listType) {
      html.push(listType === "ol" ? "</ol>" : "</ul>");
      listType = null;
    }
  };

  for (const line of lines) {
    if (line.trim().startsWith("```")) {
      closeListIfNeeded();

      if (!inCode) {
        inCode = true;
        codeBuffer = [];
      } else {
        html.push(`<pre><code>${escapeHtml(codeBuffer.join("\n"))}</code></pre>`);
        inCode = false;
      }
      continue;
    }

    if (inCode) {
      codeBuffer.push(line);
      continue;
    }

    if (!line.trim()) {
      closeListIfNeeded();
      continue;
    }

    const headingMatch = line.match(/^(#{1,6})\s+(.*)$/);
    if (headingMatch) {
      closeListIfNeeded();
      const level = headingMatch[1].length;
      html.push(`<h${level}>${inlineMarkdown(headingMatch[2])}</h${level}>`);
      continue;
    }

    const orderedMatch = line.match(/^\d+\.\s+(.*)$/);
    if (orderedMatch) {
      if (listType !== "ol") {
        closeListIfNeeded();
        html.push("<ol>");
        listType = "ol";
      }
      html.push(`<li>${inlineMarkdown(orderedMatch[1])}</li>`);
      continue;
    }

    const unorderedMatch = line.match(/^[-*]\s+(.*)$/);
    if (unorderedMatch) {
      if (listType !== "ul") {
        closeListIfNeeded();
        html.push("<ul>");
        listType = "ul";
      }
      html.push(`<li>${inlineMarkdown(unorderedMatch[1])}</li>`);
      continue;
    }

    const blockquoteMatch = line.match(/^>\s?(.*)$/);
    if (blockquoteMatch) {
      closeListIfNeeded();
      html.push(`<blockquote>${inlineMarkdown(blockquoteMatch[1])}</blockquote>`);
      continue;
    }

    closeListIfNeeded();
    html.push(`<p>${inlineMarkdown(line)}</p>`);
  }

  if (inCode) {
    html.push(`<pre><code>${escapeHtml(codeBuffer.join("\n"))}</code></pre>`);
  }

  if (listType) {
    html.push(listType === "ol" ? "</ol>" : "</ul>");
  }

  return html.join("\n");
}

function renderStats() {
  const categories = [...new Set(docs.map((doc) => doc.category))];
  docStats.innerHTML = `
    <span>${docs.length} total documents</span>
    <span>${categories.length} categories</span>
    <span>Live markdown preview enabled</span>
  `;
}

function renderNav() {
  const filteredDocs = getFilteredDocs();

  navList.innerHTML = filteredDocs
    .map((doc) => {
      const activeClass = state.activeDoc && state.activeDoc.file === doc.file ? "active" : "";
      return `
        <li>
          <button type="button" class="${activeClass}" data-file="${doc.file}">
            ${doc.title}
          </button>
        </li>
      `;
    })
    .join("");

  navList.querySelectorAll("button").forEach((button) => {
    button.addEventListener("click", () => {
      const selected = docs.find((doc) => doc.file === button.dataset.file);
      if (selected) {
        setActiveDoc(selected);
      }
    });
  });
}

function renderCards() {
  const filteredDocs = getFilteredDocs();

  if (!filteredDocs.length) {
    cardsContainer.innerHTML = "<p class=\"alert\">No matching docs found. Try a broader keyword.</p>";
    return;
  }

  cardsContainer.innerHTML = filteredDocs
    .map((doc, index) => {
      const activeClass = state.activeDoc && state.activeDoc.file === doc.file ? "active" : "";
      return `
        <article class="card ${activeClass}" data-file="${doc.file}" style="animation-delay:${Math.min(index * 40, 320)}ms">
          <p>${doc.heading}</p>
          <h4>${doc.title}</h4>
          <p>${doc.summary}</p>
          <span class="meta">${doc.category}</span>
        </article>
      `;
    })
    .join("");

  cardsContainer.querySelectorAll(".card").forEach((card) => {
    card.addEventListener("click", () => {
      const selected = docs.find((doc) => doc.file === card.dataset.file);
      if (selected) {
        setActiveDoc(selected);
      }
    });
  });
}

async function loadDocBody(doc) {
  viewerBody.innerHTML = "<p class=\"empty-state\">Loading markdown content...</p>";

  try {
    const response = await fetch(`./${doc.file}`);

    if (!response.ok) {
      throw new Error(`Could not load ${doc.file}`);
    }

    const markdown = await response.text();
    viewerBody.innerHTML = `<article class="markdown-content">${markdownToHtml(markdown)}</article>`;
  } catch (error) {
    viewerBody.innerHTML = `
      <div class="alert">
        <strong>Unable to load markdown preview.</strong><br />
        If you opened this file directly with file://, try serving docs over a local HTTP server.
      </div>
    `;
  }
}

function setActiveDoc(doc) {
  state.activeDoc = doc;

  viewerCategory.textContent = doc.category;
  viewerTitle.textContent = doc.title;
  viewerSummary.textContent = doc.summary;
  viewerSource.href = `${REPO_BASE_URL}/blob/main/docs/${doc.file}`;
  viewerSource.textContent = `Open ${doc.file} on GitHub`;

  renderNav();
  renderCards();
  loadDocBody(doc);
}

function initEvents() {
  searchInput.addEventListener("input", () => {
    state.query = normalize(searchInput.value);

    renderNav();
    renderCards();

    if (state.activeDoc && !getFilteredDocs().some((doc) => doc.file === state.activeDoc.file)) {
      const first = getFilteredDocs()[0];
      if (first) {
        setActiveDoc(first);
      }
    }
  });
}

function init() {
  renderStats();
  initEvents();
  renderNav();
  renderCards();

  const firstDoc = docs[0];
  if (firstDoc) {
    setActiveDoc(firstDoc);
  }
}

init();
