import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm, ValidatedBlobField } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IUser } from 'app/shared/model/user.model';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { IReportCard } from 'app/shared/model/report-card.model';
import { getEntity, updateEntity, createEntity, reset } from './report-card.reducer';

export const ReportCardUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const users = useAppSelector(state => state.userManagement.users);
  const reportCardEntity = useAppSelector(state => state.reportCard.entity);
  const loading = useAppSelector(state => state.reportCard.loading);
  const updating = useAppSelector(state => state.reportCard.updating);
  const updateSuccess = useAppSelector(state => state.reportCard.updateSuccess);

  const handleClose = () => {
    navigate('/report-card' + location.search);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getUsers({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  // eslint-disable-next-line complexity
  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    if (values.semester !== undefined && typeof values.semester !== 'number') {
      values.semester = Number(values.semester);
    }

    const entity = {
      ...reportCardEntity,
      ...values,
      student: users.find(it => it.id.toString() === values.student?.toString()),
      teacher: users.find(it => it.id.toString() === values.teacher?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...reportCardEntity,
          student: reportCardEntity?.student?.id,
          teacher: reportCardEntity?.teacher?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="studentPortalApp.reportCard.home.createOrEditLabel" data-cy="ReportCardCreateUpdateHeading">
            <Translate contentKey="studentPortalApp.reportCard.home.createOrEditLabel">Create or edit a ReportCard</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="report-card-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('studentPortalApp.reportCard.semester')}
                id="report-card-semester"
                name="semester"
                data-cy="semester"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('studentPortalApp.reportCard.classification')}
                id="report-card-classification"
                name="classification"
                data-cy="classification"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedBlobField
                label={translate('studentPortalApp.reportCard.pdfFile')}
                id="report-card-pdfFile"
                name="pdfFile"
                data-cy="pdfFile"
                openActionLabel={translate('entity.action.open')}
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('studentPortalApp.reportCard.comments')}
                id="report-card-comments"
                name="comments"
                data-cy="comments"
                type="text"
              />
              <ValidatedField
                id="report-card-student"
                name="student"
                data-cy="student"
                label={translate('studentPortalApp.reportCard.student')}
                type="select"
              >
                <option value="" key="0" />
                {users
                  ? users.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.login}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="report-card-teacher"
                name="teacher"
                data-cy="teacher"
                label={translate('studentPortalApp.reportCard.teacher')}
                type="select"
              >
                <option value="" key="0" />
                {users
                  ? users.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.login}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/report-card" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default ReportCardUpdate;
