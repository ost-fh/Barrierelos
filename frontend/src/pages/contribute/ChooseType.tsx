import {useTranslation} from "react-i18next";
import {useEffect, useRef, useState} from "react";
import {ContributeButton, scrollToBottom} from "../../util/formatter.tsx";
import {Box, Typography} from "@mui/material";
import ArrowCircleDownIcon from "@mui/icons-material/ArrowCircleDown";
import ChooseLevel from "./ChooseLevel.tsx";
import ChooseCategory from "./ChooseCategory.tsx";

export default function ChooseType(props: {url: string}) {
  const { url } = props;
  const {t} = useTranslation();
  const endRef = useRef<null | HTMLDivElement>(null);
  const [isGovernment, setIsGovernment] = useState(false);
  const [isPrivate, setIsPrivate] = useState(false);

  useEffect(() => {
    setIsGovernment(false);
    setIsPrivate(false);
    scrollToBottom(endRef);
  }, [url]);

  const onGovernement = () => {
    setIsPrivate(false);
    setIsGovernment(true);
  }

  const onPrivate = () => {
    setIsGovernment(false);
    setIsPrivate(true);
  }

  return (
    <>
      <Box ref={endRef} display="flex" flexDirection="column" justifyContent="center" alignItems="center">
        <ArrowCircleDownIcon color={"disabled"} sx={{mt: 4, fontSize: 80}}/>
        <Typography component="h2" variant="h5" sx={{mt: 2}}>
          {t("ContributePage.ChooseType.title")}
        </Typography>
        <Typography component="p" variant="body1" sx={{my: 2}}>
          {t("ContributePage.ChooseType.text")}
        </Typography>
        <Box display="flex" gap="16px">
          <ContributeButton onClick={onGovernement} disabled={isGovernment} variant="contained">{t("ContributePage.ChooseType.governmentButton")}</ContributeButton>
          <ContributeButton onClick={onPrivate} disabled={isPrivate} variant="contained">{t("ContributePage.ChooseType.privateButton")}</ContributeButton>
        </Box>
      </Box>
      {isGovernment && (
        <ChooseLevel
          url={url}
        />
      )}
      {isPrivate && (
        <ChooseCategory
          url={url}
        />
      )}
    </>
  )
}
