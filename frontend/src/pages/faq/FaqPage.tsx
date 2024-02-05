import {Accordion, AccordionDetails, AccordionSummary, Divider} from "@mui/material";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import {Trans} from "react-i18next";
import {ParseKeys} from "i18next";
import "./FaqPage.css";
import {useEffect} from "react";


const questionIds = [
  "barrierelos-explanation",
  "website-vs-webpage",
  "wcag-explanation",
  "barrierelos-score-explanation",
  "barrierelos-score-calculation",
]

function FaqPage() {
  useEffect(() => {
    const hash = window.location.hash;
    if (hash !== "") {
      const faqEntry = document.querySelector(hash)?.parentElement as HTMLElement | null;
      faqEntry?.focus()
    }
  }, []);

  return (
    <>
      <h1>Frequently Asked Questions (FAQ)</h1>

      {questionIds.map(questionId => (
        <Accordion
          variant="outlined"
          key={questionId}
          defaultExpanded={window.location.hash === `#${questionId}`}
          className="faq-entry"
          tabIndex={-1}
        >
          <AccordionSummary
            expandIcon={<ExpandMoreIcon/>}
            sx={{flexDirection: "row-reverse"}}
            aria-controls={`panel-${questionId}-content`}
            id={questionId}
          >
            <Trans
              i18nKey={`Faq.Questions.${questionId}.question` as ParseKeys}
              parent="span"
            />
          </AccordionSummary>
          <Divider flexItem/>
          <AccordionDetails>
            <Trans
              i18nKey={`Faq.Questions.${questionId}.answer` as ParseKeys}
              parent="span"
            />
          </AccordionDetails>
        </Accordion>
      ))}
    </>
  );
}

export default FaqPage;
