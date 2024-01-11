import {useTranslation} from "react-i18next";
import {useEffect, useRef, useState} from "react";
import {ContributeButton, scrollToBottom} from "../../util/formatter.tsx";
import {Box, Typography} from "@mui/material";
import ArrowCircleDownIcon from "@mui/icons-material/ArrowCircleDown";
import Done from "./Done.tsx";
import {Tags} from "../../model/Tags.ts";
import {WebsiteMessage} from "../../lib/api-client";
import category = WebsiteMessage.category;

export default function ChooseCountry(props: {url: string}) {
  const { url } = props;
  const {t} = useTranslation();
  const endRef = useRef<null | HTMLDivElement>(null);
  const [isSwitzerland, setIsSwitzerland] = useState(false);
  const [isLiechtenstein, setIsLiechtenstein] = useState(false);

  useEffect(() => {
    setIsSwitzerland(false);
    setIsLiechtenstein(false);
    scrollToBottom(endRef);
  }, [url]);

  const onSwitzerland = () => {
    setIsLiechtenstein(false);
    setIsSwitzerland(true);
  }

  const onLiechtenstein = () => {
    setIsSwitzerland(false);
    setIsLiechtenstein(true);
  }

  return (
    <>
      <Box ref={endRef} display="flex" flexDirection="column" justifyContent="center" alignItems="center">
        <ArrowCircleDownIcon color={"disabled"} sx={{mt: 4, fontSize: 80}}/>
        <Typography component="h2" variant="h5" sx={{mt: 2}}>
          Choose Country
        </Typography>
        <Typography component="p" variant="body1" sx={{my: 2}}>
          Choose what country this website belongs to:
        </Typography>
        <Box display="flex" gap="16px">
          <ContributeButton onClick={onSwitzerland} disabled={isSwitzerland} variant="contained">Switzerland</ContributeButton>
          <ContributeButton onClick={onLiechtenstein} disabled={isLiechtenstein} variant="contained">Liechtenstein</ContributeButton>
        </Box>
      </Box>
      {isSwitzerland && (
        <Done
          url={url}
          category={category.GOVERNMENT_FEDERAL}
          tags={[Tags.country.switzerland]}
        />
      )}
      {isLiechtenstein && (
        <Done
          url={url}
          category={category.GOVERNMENT_FEDERAL}
          tags={[Tags.country.liechtenstein]}
        />
      )}
    </>
  )
}
