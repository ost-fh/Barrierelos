import {useTranslation} from "react-i18next";
import {useEffect, useRef, useState} from "react";
import {ContributeButton, scrollToBottom} from "../../util/formatter.tsx";
import {Box, Typography} from "@mui/material";
import ArrowCircleDownIcon from "@mui/icons-material/ArrowCircleDown";
import ChooseCountry from "./ChooseCountry.tsx";
import ChooseCanton from "./ChooseCanton.tsx";
import ChooseMunicipality from "./ChooseMunicipality.tsx";

export default function ChooseLevel(props: {url: string}) {
  const { url } = props;
  const {t} = useTranslation();
  const endRef = useRef<null | HTMLDivElement>(null);
  const [isFederal, setIsFederal] = useState(false);
  const [isCantonal, setIsCantonal] = useState(false);
  const [isMunicipal, setIsMunicipal] = useState(false);

  useEffect(() => {
    setIsFederal(false);
    setIsCantonal(false);
    setIsMunicipal(false);
    scrollToBottom(endRef);
  }, [url]);

  const onFederal = () => {
    setIsCantonal(false);
    setIsMunicipal(false);
    setIsFederal(true);
  }

  const onCantonal = () => {
    setIsFederal(false);
    setIsMunicipal(false);
    setIsCantonal(true);
  }

  const onMunicipal = () => {
    setIsFederal(false);
    setIsCantonal(false);
    setIsMunicipal(true);
  }

  return (
    <>
      <Box ref={endRef} display="flex" flexDirection="column" justifyContent="center" alignItems="center">
        <ArrowCircleDownIcon color={"disabled"} sx={{mt: 4, fontSize: 80}}/>
        <Typography component="h2" variant="h5" sx={{mt: 2}}>
          Choose Level
        </Typography>
        <Typography component="p" variant="body1" sx={{my: 2}}>
          Choose whether this website is a government or a private website:
        </Typography>
        <Box display="flex" gap="16px">
          <ContributeButton onClick={onFederal} disabled={isFederal} variant="contained">Federal</ContributeButton>
          <ContributeButton onClick={onCantonal} disabled={isCantonal} variant="contained">Cantonal</ContributeButton>
          <ContributeButton onClick={onMunicipal} disabled={isMunicipal} variant="contained">Municipal</ContributeButton>
        </Box>
      </Box>
      {isFederal && (
        <ChooseCountry
          url={url}
        />
      )}
      {isCantonal && (
        <ChooseCanton
          url={url}
        />
      )}
      {isMunicipal && (
        <ChooseMunicipality
          url={url}
        />
      )}
    </>
  )
}
