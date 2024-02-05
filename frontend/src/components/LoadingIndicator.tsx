import {Box, CircularProgress} from "@mui/material";


export default function LoadingIndicator() {
  return (
    <Box sx={{marginTop: 3}}>
      <CircularProgress size={80} className="loading-indicator"/>
    </Box>
  )
}
