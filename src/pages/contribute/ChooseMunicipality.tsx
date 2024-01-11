import {useTranslation} from "react-i18next";
import {useEffect, useRef, useState} from "react";
import {municipalities, Municipality} from "../../model/Municipalities.ts";
import {scrollToBottom} from "../../util/formatter.tsx";
import {Autocomplete, Box, TextField, Typography} from "@mui/material";
import ArrowCircleDownIcon from "@mui/icons-material/ArrowCircleDown";
import Done from "./Done.tsx";
import {WebsiteMessage} from "../../lib/api-client";
import category = WebsiteMessage.category;

export default function ChooseMunicipality(props: {url: string}) {
  const { url } = props;
  const {t} = useTranslation();
  const endRef = useRef<null | HTMLDivElement>(null);
  const [municipality, setMunicipality] = useState<Municipality | undefined>(undefined);

  useEffect(() => {
    setMunicipality(undefined);
    scrollToBottom(endRef);
  }, [url]);

  const onMunicipality = (municipality: Municipality) => {
    setMunicipality(municipality);
  }

  return (
    <>
      <Box ref={endRef} display="flex" flexDirection="column" justifyContent="center" alignItems="center">
        <ArrowCircleDownIcon color={"disabled"} sx={{mt: 4, fontSize: 80}}/>
        <Typography component="h2" variant="h5" sx={{mt: 2}}>
          {t("ContributePage.ChooseMunicipality.title")}
        </Typography>
        <Typography component="p" variant="body1" sx={{my: 2}}>
          {t("ContributePage.ChooseMunicipality.text")}
        </Typography>
        <Box display="flex" gap="16px" sx={{flexGrow: 1, width: "100%", maxWidth: "320px"}}>
          <Autocomplete
            id="municipality"
            disablePortal
            options={municipalities}
            getOptionLabel={(option) => `${option.zip} ${option.municipality}`}
            fullWidth={true}
            renderInput={(params) => <TextField {...params} label={t("ContributePage.ChooseMunicipality.municipalityLabel")} />}
            onChange={(_event, newValue: Municipality | null) => {
              if(newValue) {
                onMunicipality(newValue);
              }
            }}
          />
        </Box>
      </Box>
      {municipality && (
        <Done
          url={url}
          category={category.GOVERNMENT_MUNICIPAL}
          tags={[municipality.canton]}
        />
      )}
    </>
  )
}
