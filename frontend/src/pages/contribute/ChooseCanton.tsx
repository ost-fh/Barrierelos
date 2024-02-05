import {useTranslation} from "react-i18next";
import {useEffect, useRef, useState} from "react";
import {scrollToBottom} from "../../util/formatter.tsx";
import {Box, FormControl, InputLabel, MenuItem, Select, Typography} from "@mui/material";
import ArrowCircleDownIcon from "@mui/icons-material/ArrowCircleDown";
import {Tags} from "../../model/Tags.ts";
import {ParseKeys} from "i18next";
import Done from "./Done.tsx";
import {WebsiteMessage} from "../../lib/api-client";
import category = WebsiteMessage.category;

export default function ChooseCanton(props: {url: string}) {
  const { url } = props;
  const {t} = useTranslation();
  const endRef = useRef<null | HTMLDivElement>(null);
  const [canton, setCanton] = useState<string>("");

  useEffect(() => {
    setCanton("");
    scrollToBottom(endRef);
  }, [url]);

  const onCanton = (canton: string) => {
    if(canton !== "") {
      setCanton(canton);
    }
  }

  return (
    <>
      <Box ref={endRef} display="flex" flexDirection="column" justifyContent="center" alignItems="center">
        <ArrowCircleDownIcon color={"disabled"} sx={{mt: 4, fontSize: 80}}/>
        <Typography component="h2" variant="h5" sx={{mt: 2}}>
          {t("ContributePage.ChooseCanton.title")}
        </Typography>
        <Typography component="p" variant="body1" sx={{my: 2}}>
          {t("ContributePage.ChooseCanton.text")}
        </Typography>
        <Box display="flex" gap="16px" sx={{flexGrow: 1, width: "100%", maxWidth: "320px"}}>
          <FormControl fullWidth={true}>
            <InputLabel id="label-canton">{t("ContributePage.ChooseCanton.cantonLabel")}</InputLabel>
            <Select
              id="canton"
              labelId="label-canton"
              value={canton}
              label={t("ContributePage.ChooseCanton.cantonLabel")}
              onChange={(event) => onCanton(event.target.value)}
            >
              {Object.values(Tags.canton).map(canton =>
                <MenuItem key={canton} value={canton}>{t(`Tags.${canton}` as ParseKeys).split(": ")[1]}</MenuItem>
              )}
            </Select>
          </FormControl>
        </Box>
      </Box>
      {canton !== "" && (
        <Done
          url={url}
          category={category.GOVERNMENT_CANTONAL}
          tags={[canton]}
        />
      )}
    </>
  )
}
