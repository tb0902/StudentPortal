import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, openFile, byteSize } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './report-card.reducer';

export const ReportCardDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const reportCardEntity = useAppSelector(state => state.reportCard.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="reportCardDetailsHeading">
          <Translate contentKey="studentPortalApp.reportCard.detail.title">ReportCard</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{reportCardEntity.id}</dd>
          <dt>
            <span id="semester">
              <Translate contentKey="studentPortalApp.reportCard.semester">Semester</Translate>
            </span>
          </dt>
          <dd>{reportCardEntity.semester}</dd>
          <dt>
            <span id="classification">
              <Translate contentKey="studentPortalApp.reportCard.classification">Classification</Translate>
            </span>
          </dt>
          <dd>{reportCardEntity.classification}</dd>
          <dt>
            <span id="pdfFile">
              <Translate contentKey="studentPortalApp.reportCard.pdfFile">Pdf File</Translate>
            </span>
          </dt>
          <dd>
            {reportCardEntity.pdfFile ? (
              <div>
                {reportCardEntity.pdfFileContentType ? (
                  <a onClick={openFile(reportCardEntity.pdfFileContentType, reportCardEntity.pdfFile)}>
                    <Translate contentKey="entity.action.open">Open</Translate>&nbsp;
                  </a>
                ) : null}
                <span>
                  {reportCardEntity.pdfFileContentType}, {byteSize(reportCardEntity.pdfFile)}
                </span>
              </div>
            ) : null}
          </dd>
          <dt>
            <span id="comments">
              <Translate contentKey="studentPortalApp.reportCard.comments">Comments</Translate>
            </span>
          </dt>
          <dd>{reportCardEntity.comments}</dd>
          <dt>
            <Translate contentKey="studentPortalApp.reportCard.student">Student</Translate>
          </dt>
          <dd>{reportCardEntity.student ? reportCardEntity.student.login : ''}</dd>
          <dt>
            <Translate contentKey="studentPortalApp.reportCard.teacher">Teacher</Translate>
          </dt>
          <dd>{reportCardEntity.teacher ? reportCardEntity.teacher.login : ''}</dd>
        </dl>
        <Button tag={Link} to="/report-card" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/report-card/${reportCardEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ReportCardDetail;
