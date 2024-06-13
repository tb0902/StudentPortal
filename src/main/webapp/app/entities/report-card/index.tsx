import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ReportCard from './report-card';
import ReportCardDetail from './report-card-detail';
import ReportCardUpdate from './report-card-update';
import ReportCardDeleteDialog from './report-card-delete-dialog';

const ReportCardRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ReportCard />} />
    <Route path="new" element={<ReportCardUpdate />} />
    <Route path=":id">
      <Route index element={<ReportCardDetail />} />
      <Route path="edit" element={<ReportCardUpdate />} />
      <Route path="delete" element={<ReportCardDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ReportCardRoutes;
