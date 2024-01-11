import {useTranslation} from "react-i18next";
import {useEffect, useRef, useState} from "react";
import {ContributeButton, scrollToBottom} from "../../util/formatter.tsx";
import {Box, Typography} from "@mui/material";
import ArrowCircleDownIcon from "@mui/icons-material/ArrowCircleDown";
import ChooseLevel from "./ChooseLevel.tsx";
import ChoosePrivate from "./ChoosePrivate.tsx";

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
          Choose Type
        </Typography>
        <Typography component="p" variant="body1" sx={{my: 2}}>
          Choose whether this website is a government or a private website:
        </Typography>
        <Box display="flex" gap="16px">
          <ContributeButton onClick={onGovernement} disabled={isGovernment} variant="contained">Government</ContributeButton>
          <ContributeButton onClick={onPrivate} disabled={isPrivate} variant="contained">Private</ContributeButton>
        </Box>
      </Box>
      {isGovernment && (
        <ChooseLevel
          url={url}
        />
      )}
      {isPrivate && (
        <ChoosePrivate
          url={url}
        />
      )}
    </>
  )
}
